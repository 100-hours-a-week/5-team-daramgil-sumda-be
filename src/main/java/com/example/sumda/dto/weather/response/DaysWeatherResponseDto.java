package com.example.sumda.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DaysWeatherResponseDto {
    private String maxTemperature;  // 최고 기온
    private String minTemperature;  // 최저 기온
    private int day;             // 날 (예: 3일 후의 날씨)
    private String weatherAm;    // 오전 날씨
    private String weatherPm;    // 오후 날씨

    // 정적 팩토리 메서드
    public static DaysWeatherResponseDto of(String maxTemperature, String minTemperature, int day, String weatherAm, String weatherPm) {
        return new DaysWeatherResponseDto(maxTemperature, minTemperature, day, weatherAm, weatherPm);
    }
}
