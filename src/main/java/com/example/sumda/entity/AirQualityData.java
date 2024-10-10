package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AirQualityData {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "station_name")
    private String stationName;

    // float -> Float로 변경하여 null 허용
    @Column(name = "so2")
    private Float so2;

    @Column(name = "co")
    private Float co;

    @Column(name = "o3")
    private Float o3;

    @Column(name = "no2")
    private Float no2;

    @Column(name = "pm10")
    private Float pm10;

    @Column(name = "pm25")
    private Float pm25;

    // int -> Integer로 변경하여 null 허용
    @Column(name = "so2_grade")
    private Integer so2Grade;

    @Column(name = "co_grade")
    private Integer coGrade;

    @Column(name = "o3_grade")
    private Integer o3Grade;

    @Column(name = "no2_grade")
    private Integer no2Grade;

    @Column(name = "pm10_grade")
    private Integer pm10Grade;

    @Column(name = "pm25_grade")
    private Integer pm25Grade;

    @Column(name = "khai_value")
    private Integer khaiValue;

    @Column(name = "khai_grade")
    private Integer khaiGrade;

    @Column(name = "data_time")
    private String dataTime;

}
