package com.example.windturbine.web;

import com.example.windturbine.dto.FarmSummaryDto;
import com.example.windturbine.dto.HealthAlertDto;
import com.example.windturbine.dto.HourlyMetricDto;
import com.example.windturbine.dto.TurbineHealthDto;
import com.example.windturbine.service.MonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "http://localhost:4200")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * List farms, optionally filtered by region.
     */
    @GetMapping("/farms")
    public ResponseEntity<List<FarmSummaryDto>> getFarms(
            @RequestParam(required = false) String region
    ) {
        return ResponseEntity.ok(monitoringService.getFarms(region));
    }

    /**
     * List turbines with real-time health, optionally filtered by region or farm.
     */
    @GetMapping("/turbines")
    public ResponseEntity<List<TurbineHealthDto>> getTurbines(
            @RequestParam(required = false) String region,
            @RequestParam(required = false, name = "farmCode") String farmCode
    ) {
        return ResponseEntity.ok(monitoringService.getTurbines(region, farmCode));
    }

    /**
     * Detailed real-time health for a specific turbine.
     */
    @GetMapping("/turbines/{id}/health")
    public ResponseEntity<TurbineHealthDto> getTurbineHealth(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(monitoringService.getTurbineHealth(id));
    }

    /**
     * Historical hourly metrics for a turbine between from/to.
     */
    @GetMapping("/turbines/{id}/history")
    public ResponseEntity<List<HourlyMetricDto>> getTurbineHistory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return ResponseEntity.ok(monitoringService.getTurbineHistory(id, from, to));
    }

    /**
     * Recent health alerts across the fleet, optionally filtered by turbine.
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<HealthAlertDto>> getRecentAlerts(
            @RequestParam(value = "turbineId", required = false) Long turbineId
    ) {
        return ResponseEntity.ok(monitoringService.getRecentAlerts(turbineId));
    }
}