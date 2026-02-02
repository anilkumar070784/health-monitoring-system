package com.example.windturbine.dto;

import java.time.Instant;

public record HourlyMetricDto(
        Instant hourStart,
        Double avgWindSpeedMs,
        Double avgRotorSpeedRpm,
        Double avgPowerKw,
        Double energyKwh,
        Double availability,
        Double anomalyScore,
        Boolean anomalous
) {
}

