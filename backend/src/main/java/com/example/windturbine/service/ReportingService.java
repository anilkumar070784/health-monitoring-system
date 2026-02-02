package com.example.windturbine.service;

import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.HourlyTurbineAggregate;
import com.example.windturbine.domain.Turbine;
import com.example.windturbine.dto.DailyGenerationDto;
import com.example.windturbine.repository.FarmRepository;
import com.example.windturbine.repository.HourlyTurbineAggregateRepository;
import com.example.windturbine.repository.TurbineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final FarmRepository farmRepository;
    private final TurbineRepository turbineRepository;
    private final HourlyTurbineAggregateRepository aggregateRepository;

    @Transactional(readOnly = true)
    public List<DailyGenerationDto> getDailyGeneration(LocalDate date, String farmCode) {
        Farm farm = farmRepository.findByCode(farmCode)
                .orElseThrow(() -> new EntityNotFoundException("Farm not found: " + farmCode));

        Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<DailyGenerationDto> result = new ArrayList<>();
        for (Turbine turbine : turbineRepository.findByFarm(farm)) {
            List<HourlyTurbineAggregate> aggregates =
                    aggregateRepository.findByTurbineAndHourStartBetween(turbine, from, to);

            double totalEnergy = aggregates.stream()
                    .filter(a -> a.getEnergyKwh() != null)
                    .mapToDouble(HourlyTurbineAggregate::getEnergyKwh)
                    .sum();

            double avgAvailability = aggregates.stream()
                    .filter(a -> a.getAvailability() != null)
                    .mapToDouble(HourlyTurbineAggregate::getAvailability)
                    .average()
                    .orElse(0d);

            result.add(new DailyGenerationDto(
                    turbine.getId(),
                    turbine.getCode(),
                    farm.getCode(),
                    totalEnergy,
                    avgAvailability
            ));
        }
        return result;
    }
}

