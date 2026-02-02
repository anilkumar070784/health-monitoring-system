package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "hourly_aggregates",
        indexes = {
                @Index(name = "idx_hourly_agg_turbine_hour", columnList = "turbine_id,hour_start")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class HourlyAggregate {

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
     * Energy generated during the hour in kWh.
     */
    private Double energyKwh;

    /**
     * Availability in the hour as a fraction between 0 and 1.
     */
    private Double availability;

    /**
     * Simple anomaly score (0 = normal, higher = more anomalous).
     */
    private Double anomalyScore;

    /**
     * Flag computed by anomaly detection indicating whether this hour is anomalous.
     */
    private Boolean anomalous;
}

