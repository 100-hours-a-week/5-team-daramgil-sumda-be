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
    private float so2;
    private float co;
    private float o3;
    private float no2;
    private float pm10;
    private float pm25;
    private int so2Grade;
    private int coGrade;
    private int o3Grade;
    private int no2Grade;
    private int pm10Grade;
    private int pm25Grade;
    private int khaiValue;
    private int khaiGrade;
    private Timestamp dataTime;
}