package com.example.sumda.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class TimeWeatherResponseDto {
    private String date;
    private String time;
    private Weather weather;

    @Getter
    @AllArgsConstructor
    public static class Weather {
        private String sky;
        private String precipitation;
        private String humidity;
        private String windDirection;
        private String windSpeed;

    }
}
