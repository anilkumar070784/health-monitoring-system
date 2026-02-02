package com.example.windturbine.repository;

import com.example.windturbine.domain.AnomalyAlert;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AnomalyAlertRepository extends JpaRepository<AnomalyAlert, Long> {

    // Used by AlertController to show active (unacknowledged) anomalies
    List<AnomalyAlert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    // Used by AggregationService / others for time-window queries
    List<AnomalyAlert> findByTurbineAndCreatedAtBetweenOrderByCreatedAtDesc(
            Turbine turbine,
            Instant from,
            Instant to
    );

    // Optional: last N days for a single turbine
    List<AnomalyAlert> findByTurbineAndCreatedAtAfterOrderByCreatedAtDesc(
            Turbine turbine,
            Instant since
    );

    // Optional: anomalies for a list of turbines (region / farm filtering)
    List<AnomalyAlert> findByTurbineInAndCreatedAtAfterOrderByCreatedAtDesc(
            List<Turbine> turbines,
            Instant since
    );
}