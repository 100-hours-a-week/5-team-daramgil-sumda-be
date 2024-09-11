package com.example.sumda.entity.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "air_data")
public class RedisAirData {

    @Id
    private Long id;

    private String stationName;
    private  float so2;
    private  float co;
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
    private LocalDateTime dataTime;

}
