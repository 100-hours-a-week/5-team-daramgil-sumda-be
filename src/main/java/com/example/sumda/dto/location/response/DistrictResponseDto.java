package com.example.sumda.dto.location.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistrictResponseDto {
    private Long id;
    private String district;

    public static DistrictResponseDto of(Long id, String district) {
        return new DistrictResponseDto(id, district);
    }
}
