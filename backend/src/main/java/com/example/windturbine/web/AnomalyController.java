// src/main/java/com/example/windturbine/web/AnomalyController.java
package com.example.windturbine.web;

import com.example.windturbine.dto.AnomalyCreateRequest;
import com.example.windturbine.service.AnomalyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyController {

    private final AnomalyService anomalyService;

    public AnomalyController(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @PostMapping
    public ResponseEntity<Void> createAnomaly(@RequestBody @Valid AnomalyCreateRequest request) {
        anomalyService.createAnomaly(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}