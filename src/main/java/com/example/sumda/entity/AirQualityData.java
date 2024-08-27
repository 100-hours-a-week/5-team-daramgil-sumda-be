package com.example.sumda.entity;

import groovy.lang.GString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AirQualityData {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "so2")
    private  float so2;

    @Column(name = "co")
    private  float co;

    @Column(name = "o3")
    private float o3;

    @Column(name = "no2")
    private float no2;

    @Column(name = "pm10")
    private float pm10;

    @Column(name = "pm25")
    private float pm25;

    @Column(name = "so2_grade")
    private int so2Grade;

    @Column(name = "co_grade")
    private int coGrade;

    @Column(name = "o3_grade")
    private int o3Grade;

    @Column(name = "no2_grade")
    private int no2Grade;

    @Column(name = "pm10_grade")
    private int pm10Grade;

    @Column(name = "pm25_grade")
    private int pm25Grade;

    @Column(name = "khai_value")
    private int khaiValue;

    @Column(name = "khai_grade")
    private int khaiGrade;

    @Column(name = "data_time")
    private Timestamp dataTime;

    @Column(name = "recorded_at")
    private Timestamp recorded_at;

}
