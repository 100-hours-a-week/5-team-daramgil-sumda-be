package com.example.sumda.controller;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/trigger-error")
    public String triggerError() {
        try {
            throw new Exception("This is a test exception for Sentry");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        return "Error has been captured by Sentry.";
    }
}
