package com.example.sumda.controller;

import com.example.sumda.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather/current")
    public String getWeather(@RequestParam(name = "id") Long id) {
        return weatherService.getLocationById(id);
    }
}
