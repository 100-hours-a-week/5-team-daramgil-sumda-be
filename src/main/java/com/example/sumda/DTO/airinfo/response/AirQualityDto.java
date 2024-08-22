package com.example.sumda.dto.airinfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AirQualityDto {
    private String dataTime;
    private String khaiGrade;
    private String khaiValue;
    private String pm10Grade;
    private String pm10Value;
    private String pm25Grade;
    private String pm25Value;
    private String o3Grade;
    private String o3Value;
    private String no2Grade;
    private String no2Value;
    private String coGrade;
    private String coValue;
    private String so2Grade;
    private String so2Value;

}