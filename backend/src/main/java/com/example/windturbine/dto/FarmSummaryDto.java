package com.example.windturbine.dto;

public record FarmSummaryDto(
        Long id,
        String code,
        String name,
        String region,
        long turbineCount
) {
}

