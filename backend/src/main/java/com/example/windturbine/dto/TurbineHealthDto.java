package com.example.windturbine.dto;

import java.time.Instant;

/**
 * View model representing real-time health of a turbine, including
 * latest telemetry values and simple anomaly indicators.
 *
 * This shape is used by the monitoring APIs and the Angular dashboard.
 */
public record TurbineHealthDto(
        Long id,
        String code,
        String farmCode,
        String region,
        Double latestPowerKw,
        Double latestWindSpeedMs,
        Double latestRotorSpeedRpm,
        String status,
        Instant lastUpdatedAt,
        Double lastHourEnergyKwh,
        Boolean anomalous
) {
}

