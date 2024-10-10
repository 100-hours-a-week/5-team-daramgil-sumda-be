package com.example.sumda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("{\"status\":\"new\"}");
    }
}
