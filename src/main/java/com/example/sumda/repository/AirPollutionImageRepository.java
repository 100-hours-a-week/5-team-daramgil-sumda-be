package com.example.sumda.repository;

import com.example.sumda.entity.AirPollutionImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirPollutionImageRepository extends JpaRepository<AirPollutionImages, Long> {
    List<AirPollutionImages> findByInformCode(String InformCode);
}
