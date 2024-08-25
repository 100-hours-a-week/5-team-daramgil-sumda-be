package com.example.sumda.service;

import com.example.sumda.entity.CityWeatherData;
import com.example.sumda.entity.Locations;
import com.example.sumda.repository.CityWeatherDataRepository;
import com.example.sumda.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccuWeatherService {

    private final LocationRepository locationRepository;
    private final CityWeatherDataRepository cityWeatherDataRepository;

    public CityWeatherData getCityWeatherData(Long locationId) {
        // locationId로 Locations 테이블에서 데이터를 조회
        Locations location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID: " + locationId));

        // 조회된 Location에서 city_weather_id를 추출
        Integer cityWeatherId = Math.toIntExact(location.getCityWeatherId());

        // city_weather_id로 CityWeatherData 테이블에서 데이터를 조회
        CityWeatherData cityWeatherData = cityWeatherDataRepository.findById(cityWeatherId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid city weather ID: " + cityWeatherId));

        // 조회된 데이터를 반환
        return cityWeatherData;
    }
}
