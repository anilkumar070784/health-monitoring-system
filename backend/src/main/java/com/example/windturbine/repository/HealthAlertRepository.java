package com.example.windturbine.repository;

import com.example.windturbine.domain.HealthAlert;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface HealthAlertRepository extends JpaRepository<HealthAlert, Long> {

    List<HealthAlert> findByTurbineAndCreatedAtAfterOrderByCreatedAtDesc(
            Turbine turbine,
            Instant since
    );

    List<HealthAlert> findTop20ByOrderByCreatedAtDesc();
}

