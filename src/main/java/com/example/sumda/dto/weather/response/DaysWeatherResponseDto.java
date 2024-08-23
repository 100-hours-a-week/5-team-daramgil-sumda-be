package com.example.sumda.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DaysWeatherResponseDto {
    private int maxTemperature;  // 최고 기온
    private int minTemperature;  // 최저 기온
    private int day;             // 날 (예: 3일 후의 날씨)
    private String weatherAm;    // 오전 날씨
    private String weatherPm;    // 오후 날씨

    // 정적 팩토리 메서드
    public static DaysWeatherResponseDto of(int maxTemperature, int minTemperature, int day, String weatherAm, String weatherPm) {
        return new DaysWeatherResponseDto(maxTemperature, minTemperature, day, weatherAm, weatherPm);
    }
}
