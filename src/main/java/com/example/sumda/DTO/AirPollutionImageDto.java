package com.example.sumda.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AirPollutionImageDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AirPollutionImage {
        private String informCode;
        private List<String> images;
    }

    private List<AirPollutionImage> airPollutionImages;
}
