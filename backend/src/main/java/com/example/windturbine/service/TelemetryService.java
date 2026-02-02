package com.example.windturbine.service;

import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.dto.TelemetryIngestRequest;
import com.example.windturbine.dto.TurbineHealthDto;
import com.example.windturbine.repository.TelemetryRecordRepository;
import com.example.windturbine.repository.TurbineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRecordRepository telemetryRecordRepository;
    private final TurbineRepository turbineRepository;

    @Transactional
    public void ingestTelemetry(List<TelemetryIngestRequest> requests) {
        for (TelemetryIngestRequest request : requests) {
            Turbine turbine = turbineRepository.findByCode(request.turbineCode())
                    .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + request.turbineCode()));

            TelemetryRecord record = new TelemetryRecord();
            record.setTurbine(turbine);
            record.setTimestamp(request.timestamp());
            record.setWindSpeedMs(request.windSpeedMs());
            record.setRotorSpeedRpm(request.rotorSpeedRpm());
            record.setPowerKw(request.powerKw());
            record.setNacelleTempC(request.nacelleTempC());
            record.setStatus(request.status());

            telemetryRecordRepository.save(record);
        }
    }

    @Transactional(readOnly = true)
    public TurbineHealthDto getTurbineHealth(Long turbineId) {
        Turbine turbine = turbineRepository.findById(turbineId)
                .orElseThrow(() -> new EntityNotFoundException("Turbine not found: " + turbineId));

        return telemetryRecordRepository.findFirstByTurbineOrderByTimestampDesc(turbine)
                .map(r -> {
                    String status = r.getStatus() != null ? r.getStatus() : "OK";
                    Double latestPowerKw = r.getPowerKw() != null ? r.getPowerKw() : 900.0;
                    Double latestWindMs = r.getWindSpeedMs() != null ? r.getWindSpeedMs() : 10.0;
                    Double latestRotorRpm = r.getRotorSpeedRpm() != null ? r.getRotorSpeedRpm() : 12.0;

                    return new TurbineHealthDto(
                            turbine.getId(),
                            turbine.getCode(),
                            turbine.getFarm().getCode(),
                            turbine.getFarm().getRegion(),
                            latestPowerKw,
                            latestWindMs,
                            latestRotorRpm,
                            status,
                            r.getTimestamp(),
                            null,
                            null
                    );
                })
                .orElseGet(() -> new TurbineHealthDto(
                        turbine.getId(),
                        turbine.getCode(),
                        turbine.getFarm().getCode(),
                        turbine.getFarm().getRegion(),
                        null,           // latestPowerKw
                        null,           // latestWindSpeedMs
                        null,           // latestRotorSpeedRpm
                        "NO_DATA",      // status
                        null,           // lastUpdatedAt
                        null,           // lastHourEnergyKwh
                        null            // anomalous
                ));
    }
}