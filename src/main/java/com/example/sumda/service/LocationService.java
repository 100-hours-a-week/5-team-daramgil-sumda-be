package com.example.sumda.service;

import com.example.sumda.dto.location.response.DistrictResponseDto;
import com.example.sumda.entity.Locations;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationsRepository;

    @Transactional(readOnly = true)
    public DistrictResponseDto findNearestLocations(Double latitude, Double longitude) {
        try {
            Locations locations = locationsRepository.findNearestLocation(latitude, longitude);
            if (locations == null) {
                throw new CustomException(ErrorCode.LOCATION_ERROR);
            }
            return DistrictResponseDto.of(locations.getId(), locations.getDistrict());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOCATION_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Slice<DistrictResponseDto> findSearchLocations(String district) {
        try {
            Pageable pageable = PageRequest.of(0, 10);
            Slice<Locations> locations = locationsRepository.findByLocationNameContaining(district, pageable);

            return locations.map(location -> DistrictResponseDto.of(location.getId(), location.getDistrict()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOCATION_ERROR);
        }
    }
}
