package com.example.sumda.controller;

import com.example.sumda.entity.TemperatureOutfit;
import com.example.sumda.service.TemperatureOutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;  // Use this to fetch query params
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
public class TemperatureOutfitController {

    // Autowire the service that contains the business logic
    @Autowired
    private TemperatureOutfitService temperatureOutfitService;

    // Endpoint to get outfit recommendation based on temperature
    @GetMapping("/style")
    public ResponseEntity<TemperatureOutfit> getRecommendation(@RequestParam("temperature") double temperature) {
        // Call the service method to get the outfit recommendation based on temperature
        TemperatureOutfit response = temperatureOutfitService.getRecommendation(temperature);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}