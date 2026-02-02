package com.example.windturbine.repository;

import com.example.windturbine.domain.HourlyTelemetryAggregate;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface HourlyTelemetryAggregateRepository extends JpaRepository<HourlyTelemetryAggregate, Long> {

    List<HourlyTelemetryAggregate> findByTurbineAndBucketStartBetween(
            Turbine turbine,
            Instant from,
            Instant to
    );
}

