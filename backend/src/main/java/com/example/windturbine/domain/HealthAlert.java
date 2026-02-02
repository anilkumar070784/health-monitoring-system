package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "health_alerts",
        indexes = {
                @Index(name = "idx_alert_turbine_created", columnList = "turbine_id,created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class HealthAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    private Turbine turbine;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Simple severity band: INFO, WARNING, CRITICAL, etc.
     */
    @Column(nullable = false, length = 20)
    private String severity;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private boolean acknowledged = false;
}

