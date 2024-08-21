package com.example.sumda.service;

import com.example.sumda.entity.Locations;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationsRepository;

    @Transactional(readOnly = true)
    public Locations findNearestLocations(Double latitude, Double longitude) {
        try {
            return locationsRepository.findNearestLocation(latitude, longitude);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOCATION_ERROR);
        }
    }
}
