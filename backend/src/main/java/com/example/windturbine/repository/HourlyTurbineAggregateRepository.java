package com.example.windturbine.repository;

import com.example.windturbine.domain.HourlyTurbineAggregate;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface HourlyTurbineAggregateRepository extends JpaRepository<HourlyTurbineAggregate, Long> {

    Optional<HourlyTurbineAggregate> findFirstByTurbineOrderByHourStartDesc(Turbine turbine);

    List<HourlyTurbineAggregate> findByTurbineAndHourStartBetween(
            Turbine turbine,
            Instant from,
            Instant to);
}

