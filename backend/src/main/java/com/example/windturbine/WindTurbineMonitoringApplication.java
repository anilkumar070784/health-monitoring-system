package com.example.windturbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Real-Time Wind Turbine Health Monitoring System.
 *
 * <p>
 * Scheduling is enabled for background aggregation jobs, and async processing
 * is enabled for parallel farm-level processing.
 * </p>
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class WindTurbineMonitoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(WindTurbineMonitoringApplication.class, args);
    }
}