package com.example.sumda.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentWeatherResponseDto {

    private String weather;               // 날씨 (강수형태)
    private String humidity;              // 습도
    private String precipitationLastHour; // 1시간 강수량
    private String temperature;           // 기온
    private String eastWestWind;          // 동서바람성분
    private String windDirection;         // 풍향
    private String northSouthWind;        // 남북바람성분
    private String windSpeed;             // 풍속

    public static CurrentWeatherResponseDto of(String weather, String humidity, String precipitationLastHour, String temperature, String eastWestWind, String windDirection, String northSouthWind, String windSpeed) {
        return new CurrentWeatherResponseDto(weather, humidity, precipitationLastHour, temperature, eastWestWind, windDirection, northSouthWind, windSpeed);
    }
}