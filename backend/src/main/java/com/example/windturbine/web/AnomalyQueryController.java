// src/main/java/com/example/windturbine/web/AnomalyQueryController.java
package com.example.windturbine.web;

import com.example.windturbine.domain.AnomalyAlert;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.repository.AnomalyAlertRepository;
import com.example.windturbine.repository.TurbineRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyQueryController {

    private final AnomalyAlertRepository anomalyAlertRepository;
    private final TurbineRepository turbineRepository;

    public AnomalyQueryController(AnomalyAlertRepository anomalyAlertRepository,
                                  TurbineRepository turbineRepository) {
        this.anomalyAlertRepository = anomalyAlertRepository;
        this.turbineRepository = turbineRepository;
    }

    @GetMapping
    public List<AnomalyAlert> getAnomalies(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String farmCode,
            @RequestParam(required = false) Long turbineId,
            @RequestParam(defaultValue = "24") long sinceHours
    ) {
        Instant since = Instant.now().minus(sinceHours, ChronoUnit.HOURS);

        // If turbineId is provided, use existing repo method
        if (turbineId != null) {
            Turbine t = turbineRepository.findById(turbineId)
                    .orElseThrow(() -> new IllegalArgumentException("Turbine not found: " + turbineId));
            return anomalyAlertRepository
                    .findByTurbineAndCreatedAtBetweenOrderByCreatedAtDesc(t, since, Instant.now());
        }

        // Otherwise filter by region/farm using the turbines list
        List<Turbine> turbines;
        if (farmCode != null && !farmCode.isBlank()) {
            turbines = turbineRepository.findByFarmCode(farmCode);
        } else if (region != null && !region.isBlank()) {
            turbines = turbineRepository.findByFarmRegionIgnoreCase(region);
        } else {
            turbines = turbineRepository.findAll();
        }

        return anomalyAlertRepository
                .findByTurbineInAndCreatedAtAfterOrderByCreatedAtDesc(turbines, since);
    }
}