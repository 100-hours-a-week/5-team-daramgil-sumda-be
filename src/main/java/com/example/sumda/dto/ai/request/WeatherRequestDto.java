package com.example.sumda.dto.ai.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class WeatherRequestDto {
    private String weatherType;
    private int currentTemp;
    private int highTemp;
    private int lowTemp;
}
