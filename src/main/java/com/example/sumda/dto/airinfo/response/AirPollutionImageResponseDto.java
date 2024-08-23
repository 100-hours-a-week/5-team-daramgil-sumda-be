package com.example.sumda.dto.airinfo.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AirPollutionImageResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AirPollutionImage {
        private String informCode;
        private List<String> images;
    }

    private List<AirPollutionImage> airPollutionImages;
}