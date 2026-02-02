package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "hourly_telemetry_aggregates",
        indexes = {
                @Index(name = "idx_hourly_turbine_bucket", columnList = "turbine_id,bucket_start")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class HourlyTelemetryAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    private Turbine turbine;

    /**
     * Start of the 1-hour bucket in UTC.
     */
    @Column(nullable = false)
    private Instant bucketStart;

    private Double avgWindSpeedMs;
    private Double avgRotorSpeedRpm;
    private Double avgPowerKw;
    private Double maxPowerKw;
    private Double minPowerKw;

    /**
     * Simple anomaly indicator (0 = normal, 1 = warning, 2 = critical).
     */
    private Integer anomalyLevel;
}

