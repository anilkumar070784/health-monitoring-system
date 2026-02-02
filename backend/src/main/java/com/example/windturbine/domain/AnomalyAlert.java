package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents an anomaly detected for a turbine based on aggregated data.
 */
@Entity
@Table(name = "anomaly_alerts", indexes = {
        @Index(name = "idx_alert_turbine_created", columnList = "turbine_id,created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class AnomalyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    private Turbine turbine;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 50)
    private String type; // e.g. POWER_DROP, OVER_TEMPERATURE

    @Column(nullable = false, length = 20)
    private String severity; // e.g. INFO, WARNING, CRITICAL

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "hour_start")
    private Instant hourStart;

    private boolean acknowledged;
}

