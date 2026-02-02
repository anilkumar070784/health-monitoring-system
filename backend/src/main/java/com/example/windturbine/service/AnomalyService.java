// src/main/java/com/example/windturbine/service/AnomalyService.java
package com.example.windturbine.service;

import com.example.windturbine.domain.AnomalyAlert;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.dto.AnomalyCreateRequest;
import com.example.windturbine.repository.AnomalyAlertRepository;
import com.example.windturbine.repository.TurbineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AnomalyService {

    private final TurbineRepository turbineRepository;
    private final AnomalyAlertRepository anomalyAlertRepository;

    public AnomalyService(TurbineRepository turbineRepository,
                          AnomalyAlertRepository anomalyAlertRepository) {
        this.turbineRepository = turbineRepository;
        this.anomalyAlertRepository = anomalyAlertRepository;
    }

    @Transactional
    public void createAnomaly(AnomalyCreateRequest req) {
        Turbine turbine = turbineRepository.findByCode(req.turbineCode())
                .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + req.turbineCode()));

        AnomalyAlert alert = new AnomalyAlert();
        alert.setTurbine(turbine);
        alert.setCreatedAt(Instant.now());
        alert.setHourStart(req.hourStart());
        alert.setType(req.type());
        alert.setSeverity(req.severity());
        alert.setMessage(req.message());
        alert.setAcknowledged(false);

        anomalyAlertRepository.save(alert);
    }
}