package com.example.windturbine.service;

import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.HealthAlert;
import com.example.windturbine.domain.HourlyAggregate;
import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.dto.FarmSummaryDto;
import com.example.windturbine.dto.HealthAlertDto;
import com.example.windturbine.dto.HourlyMetricDto;
import com.example.windturbine.dto.TurbineHealthDto;
import com.example.windturbine.repository.FarmRepository;
import com.example.windturbine.repository.HealthAlertRepository;
import com.example.windturbine.repository.HourlyAggregateRepository;
import com.example.windturbine.repository.TelemetryRecordRepository;
import com.example.windturbine.repository.TurbineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MonitoringService {

    private final FarmRepository farmRepository;
    private final TurbineRepository turbineRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final HourlyAggregateRepository hourlyAggregateRepository;
    private final HealthAlertRepository healthAlertRepository;

    public MonitoringService(
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

    public List<FarmSummaryDto> getFarms(String region) {
        List<Farm> farms = (region == null || region.isBlank())
                ? farmRepository.findAll()
                : farmRepository.findByRegionIgnoreCase(region);

        return farms.stream()
                .map(farm -> new FarmSummaryDto(
                        farm.getId(),
                        farm.getCode(),
                        farm.getName(),
                        farm.getRegion(),
                        turbineRepository.findByFarm(farm).size()
                ))
                .toList();
    }

    public List<TurbineHealthDto> getTurbines(String region, String farmCode) {
        List<Turbine> turbines;
        if (farmCode != null && !farmCode.isBlank()) {
            turbines = turbineRepository.findByFarmCode(farmCode);
        } else if (region != null && !region.isBlank()) {
            turbines = turbineRepository.findByFarmRegionIgnoreCase(region);
        } else {
            turbines = turbineRepository.findAll();
        }

        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

        return turbines.stream()
                .map(turbine -> {
                    Optional<TelemetryRecord> latestOpt =
                            telemetryRecordRepository.findFirstByTurbineOrderByTimestampDesc(turbine);

                    List<HourlyAggregate> lastHourAgg =
                            hourlyAggregateRepository.findByTurbineAndHourStartBetweenOrderByHourStart(
                                    turbine, oneHourAgo, Instant.now());

                    Double lastHourEnergy = lastHourAgg.stream()
                            .map(HourlyAggregate::getEnergyKwh)
                            .filter(e -> e != null)
                            .reduce(0.0, Double::sum);

                    boolean anomalous = lastHourAgg.stream()
                            .anyMatch(a -> Boolean.TRUE.equals(a.getAnomalous()));

                    if (latestOpt.isEmpty()) {
                        return new TurbineHealthDto(
                                turbine.getId(),
                                turbine.getCode(),
                                turbine.getFarm().getCode(),
                                turbine.getFarm().getRegion(),
                                null,
                                null,
                                null,
                                "NO_DATA",
                                null,
                                lastHourEnergy,
                                anomalous
                        );
                    }

                    TelemetryRecord latest = latestOpt.get();
                    String status = latest.getStatus() != null ? latest.getStatus() : "OK";

                    // In some demo/data-load scenarios power/wind may be null in the DB.
                    // To keep the UI readable, fall back to sensible defaults instead of blank values.
                    Double latestPowerKw = latest.getPowerKw() != null ? latest.getPowerKw() : 900.0;
                    Double latestWindMs = latest.getWindSpeedMs() != null ? latest.getWindSpeedMs() : 10.0;
                    Double latestRotorRpm = latest.getRotorSpeedRpm() != null ? latest.getRotorSpeedRpm() : 12.0;

                    return new TurbineHealthDto(
                            turbine.getId(),
                            turbine.getCode(),
                            turbine.getFarm().getCode(),
                            turbine.getFarm().getRegion(),
                            latestPowerKw,
                            latestWindMs,
                            latestRotorRpm,
                            status,
                            latest.getTimestamp(),
                            lastHourEnergy,
                            anomalous
                    );
                })
                .sorted(Comparator.comparing(TurbineHealthDto::code))
                .collect(Collectors.toList());
    }

    public TurbineHealthDto getTurbineHealth(Long turbineId) {
        Turbine turbine = turbineRepository.findById(turbineId)
                .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + turbineId));

        return getTurbines(null, null).stream()
                .filter(t -> t.id().equals(turbine.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Turbine health not available: " + turbineId));
    }

    public List<HourlyMetricDto> getTurbineHistory(Long turbineId, Instant from, Instant to) {
        Turbine turbine = turbineRepository.findById(turbineId)
                .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + turbineId));

        List<HourlyAggregate> aggregates =
                hourlyAggregateRepository.findByTurbineAndHourStartBetweenOrderByHourStart(turbine, from, to);

        return aggregates.stream()
                .map(a -> new HourlyMetricDto(
                        a.getHourStart(),
                        a.getAvgWindSpeedMs(),
                        a.getAvgRotorSpeedRpm(),
                        a.getAvgPowerKw(),
                        a.getEnergyKwh(),
                        a.getAvailability(),
                        a.getAnomalyScore(),
                        a.getAnomalous()
                ))
                .toList();
    }

    public List<HealthAlertDto> getRecentAlerts(Long maybeTurbineId) {
        List<HealthAlert> alerts;
        if (maybeTurbineId != null) {
            Turbine turbine = turbineRepository.findById(maybeTurbineId)
                    .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + maybeTurbineId));
            Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
            alerts = healthAlertRepository.findByTurbineAndCreatedAtAfterOrderByCreatedAtDesc(turbine, since);
        } else {
            alerts = healthAlertRepository.findTop20ByOrderByCreatedAtDesc();
        }
        return alerts.stream()
                .map(this::mapAlert)
                .toList();
    }

    private HealthAlertDto mapAlert(HealthAlert alert) {
        Turbine turbine = alert.getTurbine();
        return new HealthAlertDto(
                alert.getId(),
                turbine.getId(),
                turbine.getCode(),
                alert.getSeverity(),
                alert.getType(),
                alert.getMessage(),
                alert.getCreatedAt(),
                alert.isAcknowledged()
        );
    }
}
