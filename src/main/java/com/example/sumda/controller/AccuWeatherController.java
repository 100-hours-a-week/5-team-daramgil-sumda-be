package com.example.sumda.controller;

import com.example.sumda.entity.CityWeatherData;
import com.example.sumda.service.AccuWeatherService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acweather")
@RequiredArgsConstructor
public class AccuWeatherController {

    private final AccuWeatherService accuWeatherService;

    @GetMapping("")
    public ResponseEntity<CityWeatherData> getAccuWeather(@RequestParam("id") Long id) {
        // Service를 호출하여 비즈니스 로직을 처리하고 결과를 반환합니다.
        CityWeatherData weatherData = accuWeatherService.getCityWeatherData(id);
        return ResponseEntity.ok(weatherData);
    }
}
