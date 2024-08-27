package com.example.sumda.repository;


import com.example.sumda.entity.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirQualityDataRepository extends JpaRepository<AirQualityData, Long> {
}
