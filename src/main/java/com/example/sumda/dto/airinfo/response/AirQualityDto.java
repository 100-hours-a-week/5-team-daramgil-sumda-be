package com.example.sumda.dto.airinfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AirQualityDto {
    private int id;
    private String station_name;
    private String so2;
    private String co;
    private String o3;
    private String no2;
    private String pm10;
    private String pm25;
    private String so2Grade;
    private String coGrade;
    private String o3Grade;
    private String no2Grade;
    private String pm10Grade;
    private String pm25Grade;
    private String khaiValue;
    private String khaiGrade;
    private LocalDateTime dataTime;
}