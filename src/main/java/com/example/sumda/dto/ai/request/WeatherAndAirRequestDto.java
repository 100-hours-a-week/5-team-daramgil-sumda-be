package com.example.sumda.dto.ai.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class WeatherAndAirRequestDto {
    private int khaiGrade;          // 대기환경지수 등급
    private int khaiValue;          // 대기환경지수 값
    private String sensitiveGroup;     // 민감군 여부 (0: 일반, 1: 민감군)
    private String weatherType;     // 날씨 타입 (예: 맑음, 흐림, 비 등)
    private double currentTemperature; // 현재 온도
    private Long locationId;         // 지역 id
}