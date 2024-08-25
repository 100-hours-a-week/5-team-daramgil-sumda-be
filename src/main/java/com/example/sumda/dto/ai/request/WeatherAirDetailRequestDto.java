package com.example.sumda.dto.ai.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public class WeatherAirDetailRequestDto {
    private AirQuality airQuality;
    private CurrentWeather currentWeather;

    @Getter
    @AllArgsConstructor
    public static class AirQuality {
        private String khaiValue;      // 종합 대기 질 지수
        private String khaiGrade;   // 종합 대기 질 등급
        private AirQualityDetail pm10; // PM10 (미세먼지) 정보
        private AirQualityDetail pm25; // PM2.5 (초미세먼지) 정보

        @Getter
        @AllArgsConstructor
        public static class AirQualityDetail {
            private String value;         // PM10/PM2.5 값
            private String grade;      // PM10/PM2.5 등급
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CurrentWeather {
        private String weatherType;    // 날씨 타입 (예: 맑음)
        private String currentTemp;       // 현재 온도
        private String highTemp;          // 최고 온도
        private String lowTemp;           // 최저 온도
    }

    // 데이터를 문장 형식으로 출력하는 메서드
    @Override
    public String toString() {
        return "현재 날씨는 " + currentWeather.getWeatherType() + "이며, 현재 온도는 " +
                currentWeather.getCurrentTemp() + "도입니다. 최고 온도는 " +
                currentWeather.getHighTemp() + "도이고, 최저 온도는 " +
                currentWeather.getLowTemp() + "도입니다. 대기 질 정보로는 종합 대기 질 지수가 " +
                airQuality.getKhaiValue() + "이며, 등급은 " + airQuality.getKhaiGrade() + "입니다. " +
                "PM10(미세먼지) 수치는 " + airQuality.getPm10().getValue() + "로, 등급은 " +
                airQuality.getPm10().getGrade() + "입니다. PM2.5(초미세먼지) 수치는 " +
                airQuality.getPm25().getValue() + "로, 등급은 " +
                airQuality.getPm25().getGrade() + "입니다.";
    }
}