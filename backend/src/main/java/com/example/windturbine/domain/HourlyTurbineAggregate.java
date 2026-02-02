package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Hourly aggregate of high-frequency telemetry for a single turbine.
 *
 * <p>
 * Aggregates are used for performance analysis, daily generation metrics,
 * and anomaly detection.
 * </p>
 */
@Entity
@Table(
        name = "hourly_turbine_aggregates",
        indexes = {
                @Index(name = "idx_agg_turbine_hour", columnList = "turbine_id,hour_start")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class HourlyTurbineAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    private Turbine turbine;

    /**
     * Start of the hour (UTC) this aggregate represents.
     */
    @Column(name = "hour_start", nullable = false)
    private Instant hourStart;

    private Double avgWindSpeedMs;
    private Double avgRotorSpeedRpm;
    private Double avgPowerKw;

    /**
     * Total energy generated in this hour (kWh).
     */
    private Double energyKwh;

    /**
     * Fraction of the hour where the turbine was producing power (0-1).
     */
    private Double availability;

    /**
     * Simple anomaly indicator computed from this aggregate (optional).
     */
    private Boolean anomalyDetected;
}

