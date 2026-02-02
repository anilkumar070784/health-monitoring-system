package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "telemetry_records",
        indexes = {
                @Index(name = "idx_telemetry_turbine_ts", columnList = "turbine_id,timestamp")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    private Turbine turbine;

    @Column(nullable = false)
    private Instant timestamp;

    private Double windSpeedMs;
    private Double rotorSpeedRpm;
    private Double powerKw;
    private Double nacelleTempC;

    @Column(length = 50)
    private String status; // e.g. OK, WARNING, FAULT
}

