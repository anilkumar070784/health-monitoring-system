package com.example.windturbine.dto;

import java.time.Instant;

public record HealthAlertDto(
        Long id,
        Long turbineId,
        String turbineCode,
        String severity,
        String type,
        String message,
        Instant createdAt,
        boolean acknowledged
) {
}

