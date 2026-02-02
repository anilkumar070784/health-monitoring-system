package com.example.windturbine.web;

import com.example.windturbine.dto.DailyGenerationDto;
import com.example.windturbine.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    /**
     * Daily generation and efficiency metrics per turbine for a farm.
     */
    @GetMapping("/daily-generation")
    public List<DailyGenerationDto> getDailyGeneration(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String farmCode
    ) {
        return reportingService.getDailyGeneration(date, farmCode);
    }
}

