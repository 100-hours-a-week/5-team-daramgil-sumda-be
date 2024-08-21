package com.example.sumda.service;

import com.example.sumda.entity.AirQualityStations;
import com.example.sumda.repository.AirQualityStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {
    private final AirQualityStationRepository airQualityStationRepository;

//    public Slice<AirQualityStations> getStationContains(String stationName, int page, int size) {
//        Pageable pageable = PageRequest.of(page,size);
//        return airQualityStationRepository.findByStationNameContaining(stationName,pageable);
//    }

}
