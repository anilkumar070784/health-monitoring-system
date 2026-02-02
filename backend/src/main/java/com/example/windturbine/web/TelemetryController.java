package com.example.windturbine.web;

import com.example.windturbine.dto.TelemetryIngestRequest;
import com.example.windturbine.service.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    /**
     * Ingest a batch of telemetry records (e.g. 10-second data).
     */
    @PostMapping
    public ResponseEntity<Void> ingestTelemetry(
            @RequestBody @Valid List<TelemetryIngestRequest> payload
    ) {
        telemetryService.ingestTelemetry(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

