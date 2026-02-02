// src/main/java/com/example/windturbine/dto/AnomalyCreateRequest.java
package com.example.windturbine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AnomalyCreateRequest(
        @NotBlank String turbineCode,
        @NotNull Instant hourStart,
        @NotBlank String type,      // e.g. LOW_PERFORMANCE, OVER_TEMPERATURE
        @NotBlank String severity,  // INFO, WARNING, CRITICAL
        @NotBlank String message
) {}