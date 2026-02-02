package com.example.windturbine.repository;

import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {

    Optional<TelemetryRecord> findFirstByTurbineOrderByTimestampDesc(Turbine turbine);

    List<TelemetryRecord> findTop1ByTurbineOrderByTimestampDesc(Turbine turbine);

    List<TelemetryRecord> findByTurbineAndTimestampBetween(
            Turbine turbine,
            Instant from,
            Instant to
    );

    @Query("select t from TelemetryRecord t " +
            "where t.turbine = :turbine and t.timestamp >= :from and t.timestamp < :to " +
            "order by t.timestamp asc")
    List<TelemetryRecord> findForTurbineBetween(
            @Param("turbine") Turbine turbine,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
            select tr from TelemetryRecord tr
            where tr.timestamp >= :from and tr.timestamp < :to
            order by tr.turbine.id, tr.timestamp
            """)
    List<TelemetryRecord> findAllForAggregationWindow(
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}

