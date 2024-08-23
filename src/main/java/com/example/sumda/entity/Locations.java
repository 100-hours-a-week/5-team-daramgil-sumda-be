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
    private int code;

    @ManyToOne
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private AirQualityStations station;

}