package com.example.windturbine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "turbines")
@Getter
@Setter
@NoArgsConstructor
public class Turbine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String model;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @Column(nullable = false)
    private Double ratedPowerKw;

    @OneToMany(mappedBy = "turbine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TelemetryRecord> telemetryRecords;
}

