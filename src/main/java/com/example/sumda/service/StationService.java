package com.example.sumda.service;

import com.example.sumda.entity.Station;
import com.example.sumda.repository.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public Slice<Station> getStationContains(String stationName, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return stationRepository.findByStationNameContaining(stationName,pageable);
    }

}
