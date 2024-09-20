package com.example.sumda.repository;


import com.example.sumda.entity.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AirQualityDataRepository extends JpaRepository<AirQualityData, Long> {
    List<AirQualityData> findByStationName(String stationName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE air_quality_data SET data_time = NOW() " +
            "WHERE station_name = :stationName AND data_time = '0000-00-00 00:00:00'", nativeQuery = true)
    void updateZeroDateValues(@Param("stationName") String stationName);
}
