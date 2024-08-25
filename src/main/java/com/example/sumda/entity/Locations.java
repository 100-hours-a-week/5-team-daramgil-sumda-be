package com.example.sumda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
public class Locations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district")
    private String district;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "nx")
    private int nx;

    @Column(name = "ny")
    private int ny;

    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private AirQualityStations station;

//  아큐웨더 api 요청하고 저장하고 있는 데이터 테이블인 city_weather_data 의 ID 이지만 급해서 아직 외래키 연결 안해두었습니다.
    @Column(name = "city_weather_id")
    private Long cityWeatherId;
}