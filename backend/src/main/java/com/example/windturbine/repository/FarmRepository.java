package com.example.windturbine.repository;

import com.example.windturbine.domain.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Optional<Farm> findByCode(String code);

    List<Farm> findByRegion(String region);

    List<Farm> findByRegionIgnoreCase(String region);
}

