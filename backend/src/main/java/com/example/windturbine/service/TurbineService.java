package com.example.windturbine.service;

import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.TelemetryRecord;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.repository.FarmRepository;
import com.example.windturbine.repository.TelemetryRecordRepository;
import com.example.windturbine.repository.TurbineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TurbineService {

    private final TurbineRepository turbineRepository;
    private final FarmRepository farmRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;

    public TurbineService(TurbineRepository turbineRepository,
                          FarmRepository farmRepository,
                          TelemetryRecordRepository telemetryRecordRepository) {
        this.turbineRepository = turbineRepository;
        this.farmRepository = farmRepository;
        this.telemetryRecordRepository = telemetryRecordRepository;
    }

    public List<Turbine> getAllTurbines() {
        return turbineRepository.findAll();
    }

    public List<Farm> getAllFarms() {
        return farmRepository.findAll();
    }

    public List<Farm> getFarmsByRegion(String region) {
        return farmRepository.findByRegion(region);
    }

    public List<Turbine> getTurbinesByRegion(String region) {
        return turbineRepository.findByFarmRegion(region);
    }

    public Optional<Turbine> getTurbine(Long id) {
        return turbineRepository.findById(id);
    }

    public Optional<TelemetryRecord> getLatestTelemetry(Turbine turbine) {
        List<TelemetryRecord> latest = telemetryRecordRepository.findTop1ByTurbineOrderByTimestampDesc(turbine);
        return latest.isEmpty() ? Optional.empty() : Optional.of(latest.get(0));
    }
}

