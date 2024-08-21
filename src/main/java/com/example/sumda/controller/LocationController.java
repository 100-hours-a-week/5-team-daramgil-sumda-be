package com.example.sumda.controller;

import com.example.sumda.entity.Locations;
import com.example.sumda.service.LocationService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/convert")
    public ResponseEntity<?> findNearestLocations(@RequestParam Double latitude, @RequestParam Double longitude) {
        System.out.println("latitude: " + latitude + ", longitude: " + longitude);
        return ResponseUtils.createResponse(HttpStatus.OK,"가장 가까운 위치를 찾았습니다.", locationService.findNearestLocations(latitude, longitude));
    }

    @GetMapping("/search")
    public ResponseEntity<?> findSearchLocations(@RequestParam String query) {
        return ResponseUtils.createResponse(HttpStatus.OK,"검색한 위치를 찾았습니다.", locationService.findSearchLocations(query).getContent());
    }
}
