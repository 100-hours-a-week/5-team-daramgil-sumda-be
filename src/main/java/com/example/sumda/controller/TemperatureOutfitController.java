package com.example.sumda.controller;

import com.example.sumda.entity.TemperatureOutfit;
import com.example.sumda.service.TemperatureOutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class TemperatureOutfitController {

    // Autowire the service that contains the business logic
    @Autowired
    private TemperatureOutfitService temperatureOutfitService;

    // Endpoint to get all outfit recommendations for different temperature ranges
    @GetMapping("/style")
    public ResponseEntity<List<TemperatureOutfit>> getAllRecommendations() {
        // Call the service method to get all outfit recommendations
        List<TemperatureOutfit> response = temperatureOutfitService.getAllRecommendations();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}