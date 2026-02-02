package com.example.windturbine.web.dto;

import java.time.Instant;

public record TurbineHealthDto(
        Long id,
        String code,
        String model,
        String farmCode,
        String farmName,
        String region,
        Double latestPowerKw,
        Double latestWindSpeedMs,
        Double latestRotorSpeedRpm,
        String latestStatus,
        Instant latestTimestamp
) {
}

