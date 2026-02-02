package com.example.windturbine.repository;

import com.example.windturbine.domain.HourlyAggregate;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface HourlyAggregateRepository extends JpaRepository<HourlyAggregate, Long> {

    List<HourlyAggregate> findByTurbineAndHourStartBetweenOrderByHourStart(
            Turbine turbine,
            Instant from,
            Instant to
    );
}

