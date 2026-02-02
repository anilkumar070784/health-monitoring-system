package com.example.windturbine.service;

import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.HealthAlert;
import com.example.windturbine.domain.HourlyAggregate;
import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.repository.FarmRepository;
import com.example.windturbine.repository.HealthAlertRepository;
import com.example.windturbine.repository.HourlyAggregateRepository;
import com.example.windturbine.repository.TelemetryRecordRepository;
import com.example.windturbine.repository.TurbineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TelemetryAggregationService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryAggregationService.class);

    private final FarmRepository farmRepository;
    private final TurbineRepository turbineRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final HourlyAggregateRepository hourlyAggregateRepository;
    private final HealthAlertRepository healthAlertRepository;

    private final ExecutorService farmExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public TelemetryAggregationService(
            FarmRepository farmRepository,
            TurbineRepository turbineRepository,
            TelemetryRecordRepository telemetryRecordRepository,
            HourlyAggregateRepository hourlyAggregateRepository,
            HealthAlertRepository healthAlertRepository
    ) {
        this.farmRepository = farmRepository;
        this.turbineRepository = turbineRepository;
        this.telemetryRecordRepository = telemetryRecordRepository;
        this.hourlyAggregateRepository = hourlyAggregateRepository;
        this.healthAlertRepository = healthAlertRepository;
    }

    /**
     * Aggregate 10-second telemetry into hourly buckets across all farms in parallel.
     * This can be wired to a scheduler if needed.
     */
    @Transactional
    public void aggregateLastHour() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.HOURS);
        Instant from = now.minus(1, ChronoUnit.HOURS);
        Instant to = now;

        log.info("Starting hourly aggregation window from {} to {}", from, to);

        List<Farm> farms = farmRepository.findAll();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Farm farm : farms) {
            CompletableFuture<Void> f = CompletableFuture.runAsync(
                    () -> aggregateFarmForWindow(farm, from, to),
                    farmExecutor
            );
            futures.add(f);
        }

        // Wait for all farms to complete
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        log.info("Completed hourly aggregation for all farms");
    }

    private void aggregateFarmForWindow(Farm farm, Instant from, Instant to) {
        log.debug("Aggregating farm {} ({}) for window {}", farm.getCode(), farm.getRegion(), from);

        List<Turbine> turbines = turbineRepository.findByFarm(farm);
        if (turbines.isEmpty()) {
            return;
        }

        List<TelemetryRecord> records = telemetryRecordRepository.findAllForAggregationWindow(from, to);

        // Group by turbine
        Map<Long, List<TelemetryRecord>> byTurbine = records.stream()
                .filter(r -> r.getTurbine() != null && r.getTurbine().getFarm().getId().equals(farm.getId()))
                .collect(Collectors.groupingBy(r -> r.getTurbine().getId()));

        for (Turbine turbine : turbines) {
            List<TelemetryRecord> turbineRecords = byTurbine.get(turbine.getId());
            if (turbineRecords == null || turbineRecords.isEmpty()) {
                continue;
            }

            HourlyAggregate aggregate = buildAggregate(turbine, from, turbineRecords);
            hourlyAggregateRepository.save(aggregate);

            // Simple anomaly detection rule: if availability < 0.95 or energy is far below capacity, flag
            boolean anomalous = Boolean.TRUE.equals(aggregate.getAnomalous());
            if (anomalous) {
                HealthAlert alert = new HealthAlert();
                alert.setTurbine(turbine);
                alert.setCreatedAt(Instant.now());
                alert.setSeverity("WARNING");
                alert.setType("ANOMALY");
                alert.setMessage("Potential anomaly detected for turbine " + turbine.getCode()
                        + " in hour starting " + from);
                healthAlertRepository.save(alert);
            }
        }
    }

    private HourlyAggregate buildAggregate(Turbine turbine, Instant hourStart, List<TelemetryRecord> turbineRecords) {
        HourlyAggregate aggregate = new HourlyAggregate();
        aggregate.setTurbine(turbine);
        aggregate.setHourStart(hourStart);

        DoubleSummaryStatistics windStats = turbineRecords.stream()
                .map(TelemetryRecord::getWindSpeedMs)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics rotorStats = turbineRecords.stream()
                .map(TelemetryRecord::getRotorSpeedRpm)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics powerStats = turbineRecords.stream()
                .map(TelemetryRecord::getPowerKw)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        if (windStats.getCount() > 0) {
            aggregate.setAvgWindSpeedMs(windStats.getAverage());
        }
        if (rotorStats.getCount() > 0) {
            aggregate.setAvgRotorSpeedRpm(rotorStats.getAverage());
        }
        if (powerStats.getCount() > 0) {
            aggregate.setAvgPowerKw(powerStats.getAverage());
        }

        // Assuming samples every 10 seconds: 360 samples per hour.
        long sampleCount = powerStats.getCount();
        if (sampleCount > 0 && aggregate.getAvgPowerKw() != null) {
            double seconds = sampleCount * 10.0;
            double hours = seconds / 3600.0;
            double energyKwh = aggregate.getAvgPowerKw() * hours;
            aggregate.setEnergyKwh(energyKwh);
        }

        // Availability: proportion of records where status is not FAULT
        long okOrWarn = turbineRecords.stream()
                .filter(r -> r.getStatus() == null
                        || (!"FAULT".equalsIgnoreCase(r.getStatus())))
                .count();
        double availability = (double) okOrWarn / turbineRecords.size();
        aggregate.setAvailability(availability);

        // Very simple anomaly heuristic
        double anomalyScore = 0.0;
        if (availability < 0.95) {
            anomalyScore += (0.95 - availability) * 10;
        }
        if (aggregate.getAvgPowerKw() != null && turbine.getRatedPowerKw() != null) {
            double expectedMin = turbine.getRatedPowerKw() * 0.2; // 20% of rated
            if (aggregate.getAvgPowerKw() < expectedMin) {
                anomalyScore += (expectedMin - aggregate.getAvgPowerKw()) / expectedMin * 10;
            }
        }

        aggregate.setAnomalyScore(anomalyScore);
        aggregate.setAnomalous(anomalyScore > 5.0);

        return aggregate;
    }
}

