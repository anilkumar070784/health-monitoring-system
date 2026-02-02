package com.example.windturbine.repository;

import com.example.windturbine.domain.Farm;
import com.example.windturbine.domain.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurbineRepository extends JpaRepository<Turbine, Long> {

    Optional<Turbine> findByCode(String code);

    List<Turbine> findByFarm(Farm farm);

    List<Turbine> findByFarmRegion(String region);

    List<Turbine> findByFarmRegionIgnoreCase(String region);

    List<Turbine> findByFarmCode(String farmCode);
}

