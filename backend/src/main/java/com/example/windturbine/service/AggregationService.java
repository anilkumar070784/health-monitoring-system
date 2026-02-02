package com.example.windturbine.service;

import com.example.windturbine.domain.AnomalyAlert;
import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.HourlyTurbineAggregate;
import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.repository.AnomalyAlertRepository;
import com.example.windturbine.repository.FarmRepository;
import com.example.windturbine.repository.HourlyTurbineAggregateRepository;
import com.example.windturbine.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Converts high-frequency telemetry into hourly aggregates and performs
 * simple anomaly detection in parallel across farms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private final FarmRepository farmRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final HourlyTurbineAggregateRepository aggregateRepository;
    private final AnomalyAlertRepository anomalyAlertRepository;
    private final ThreadPoolTaskExecutor aggregationExecutor;

    /**
     * Run every 5 minutes and process the previous complete hour.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void aggregateLastHourJob() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0);
        Instant to = nowUtc.toInstant();
        Instant from = nowUtc.minusHours(1).toInstant();

        log.info("Starting aggregation job for window {} - {}", from, to);

        List<Farm> farms = farmRepository.findAll();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Farm farm : farms) {
            futures.add(CompletableFuture.runAsync(() -> aggregateFarmWindow(farm, from, to), aggregationExecutor));
        }

        // Wait for all farms to complete
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        log.info("Completed aggregation job for window {} - {}", from, to);
    }

    @Transactional
    protected void aggregateFarmWindow(Farm farm, Instant from, Instant to) {
        log.debug("Aggregating farm {} between {} and {}", farm.getCode(), from, to);
        for (Turbine turbine : farm.getTurbines()) {
            aggregateTurbineWindow(turbine, from, to);
        }
    }

    @Transactional
    protected void aggregateTurbineWindow(Turbine turbine, Instant from, Instant to) {
        List<TelemetryRecord> records = telemetryRecordRepository.findForTurbineBetween(turbine, from, to);
        if (records.isEmpty()) {
            return;
        }

        DoubleSummaryStatistics powerStats = records.stream()
                .filter(r -> r.getPowerKw() != null)
                .mapToDouble(TelemetryRecord::getPowerKw)
                .summaryStatistics();

        DoubleSummaryStatistics windStats = records.stream()
                .filter(r -> r.getWindSpeedMs() != null)
                .mapToDouble(TelemetryRecord::getWindSpeedMs)
                .summaryStatistics();

        DoubleSummaryStatistics rotorStats = records.stream()
                .filter(r -> r.getRotorSpeedRpm() != null)
                .mapToDouble(TelemetryRecord::getRotorSpeedRpm)
                .summaryStatistics();

        long producingCount = records.stream()
                .filter(r -> r.getPowerKw() != null && r.getPowerKw() > 0.1)
                .count();

        HourlyTurbineAggregate aggregate = new HourlyTurbineAggregate();
        aggregate.setTurbine(turbine);
        aggregate.setHourStart(from);
        aggregate.setAvgPowerKw(powerStats.getCount() == 0 ? null : powerStats.getAverage());
        aggregate.setAvgWindSpeedMs(windStats.getCount() == 0 ? null : windStats.getAverage());
        aggregate.setAvgRotorSpeedRpm(rotorStats.getCount() == 0 ? null : rotorStats.getAverage());

        // Rough approximation: power average (kW) * 1h -> kWh
        aggregate.setEnergyKwh(aggregate.getAvgPowerKw() == null ? null : aggregate.getAvgPowerKw());

        double availability = records.isEmpty() ? 0d : (double) producingCount / records.size();
        aggregate.setAvailability(availability);

        boolean anomaly = detectSimpleAnomaly(turbine, aggregate);
        aggregate.setAnomalyDetected(anomaly);

        aggregateRepository.save(aggregate);

        if (anomaly) {
            createAnomalyAlert(turbine, aggregate);
        }
    }

    private boolean detectSimpleAnomaly(Turbine turbine, HourlyTurbineAggregate aggregate) {
        if (aggregate.getAvgPowerKw() == null || turbine.getRatedPowerKw() == null) {
            return false;
        }
        double ratio = aggregate.getAvgPowerKw() / turbine.getRatedPowerKw();
        // Very simple rule-based detection: average power < 20% of rated power
        // while availability is reasonably high.
        return aggregate.getAvailability() != null
                && aggregate.getAvailability() > 0.7
                && ratio < 0.2;
    }

    private void createAnomalyAlert(Turbine turbine, HourlyTurbineAggregate aggregate) {
        AnomalyAlert alert = new AnomalyAlert();
        alert.setTurbine(turbine);
        alert.setCreatedAt(Instant.now());
        alert.setHourStart(aggregate.getHourStart());
        alert.setType("LOW_PERFORMANCE");
        alert.setSeverity("WARNING");
        alert.setMessage("Average power below 20% of rated capacity with high availability");
        alert.setAcknowledged(false);
        anomalyAlertRepository.save(alert);
    }
}

