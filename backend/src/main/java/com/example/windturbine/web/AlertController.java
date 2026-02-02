package com.example.windturbine.web;

import com.example.windturbine.domain.AnomalyAlert;
import com.example.windturbine.repository.AnomalyAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AnomalyAlertRepository anomalyAlertRepository;

    @GetMapping
    public List<AnomalyAlert> listActiveAlerts() {
        return anomalyAlertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc();
    }

    @PostMapping("/{id}/ack")
    public ResponseEntity<?> acknowledge(@PathVariable Long id) {
        return anomalyAlertRepository.findById(id)
                .map(alert -> {
                    alert.setAcknowledged(true);
                    anomalyAlertRepository.save(alert);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

