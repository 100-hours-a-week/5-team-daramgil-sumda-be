package com.example.sumda.controller;

import com.example.sumda.service.WeatherService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(@RequestParam("id") Long id) {
        return ResponseUtils.createResponse(HttpStatus.OK, "현재 날씨 간단 조회 성공",weatherService.getCurrentWeather(id));
    }

    @GetMapping("/days")
    public ResponseEntity<?> getWeatherDays(@RequestParam("id") Long id) {
        return ResponseUtils.createResponse(HttpStatus.OK, "날씨 예보 조회 성공",weatherService.getDaysWeatherByID(id));
    }

    @GetMapping("/time")
    public ResponseEntity<?> getTimeWeather(@RequestParam("id") Long id) {
        return ResponseUtils.createResponse(HttpStatus.OK, "시간별 날씨 조회 성공",weatherService.getTimeWeather(id));
    }

}
