package com.example.windturbine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request payload for ingesting raw telemetry.
 */
public record TelemetryIngestRequest(

        @NotBlank
        String turbineCode,

        /**
         * Measurement timestamp in ISO-8601 (UTC).
         */
        @NotNull
        Instant timestamp,

        Double windSpeedMs,
        Double rotorSpeedRpm,
        Double powerKw,
        Double nacelleTempC,
        String status
) {
}

