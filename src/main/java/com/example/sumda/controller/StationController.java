package com.example.sumda.controller;

import com.example.sumda.service.StationService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @GetMapping
    public ResponseEntity<?> getAllStation(@RequestParam String stationName, @RequestParam int page, @RequestParam int size) {

        return ResponseUtils.createResponse(HttpStatus.OK, "Success", stationService.getStationContains(stationName, page, size));
    }
}
