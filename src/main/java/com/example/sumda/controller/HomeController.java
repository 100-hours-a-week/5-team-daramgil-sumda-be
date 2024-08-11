package com.example.sumda.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/api")
    public String home() {
        return "Hello, Spring Boot!";
    }
}
