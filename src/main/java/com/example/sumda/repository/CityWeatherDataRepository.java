package com.example.sumda.repository;

import com.example.sumda.entity.CityWeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CityWeatherDataRepository extends JpaRepository<CityWeatherData, Integer> {
    Optional<CityWeatherData> findById(Integer id);
}
