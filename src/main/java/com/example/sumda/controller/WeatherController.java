package com.example.sumda.controller;

import com.example.sumda.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather() {

        weatherService.getDaysWeatherByID(1L);
        return ResponseEntity.ok("Current Weather");
    }
}
