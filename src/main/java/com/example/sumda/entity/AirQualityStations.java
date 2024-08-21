package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AirQualityStations {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "dm_x")
    private Double dmX;

    @Column(name = "dm_y")
    private Double dmY;

    @Column(name = "addr")
    private String addr;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "mang_name")
    private String mangName;

    @Column(name = "year")
    private Integer year;

    @Column(name = "item")
    private String item;
}