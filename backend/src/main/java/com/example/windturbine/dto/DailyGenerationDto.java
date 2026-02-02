package com.example.windturbine.dto;

/**
 * Daily energy generation and efficiency metrics for a turbine.
 */
public record DailyGenerationDto(
        Long turbineId,
        String turbineCode,
        String farmCode,
        Double totalEnergyKwh,
        Double averageAvailability
) {
}

