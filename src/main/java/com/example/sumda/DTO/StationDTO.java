package com.example.sumda.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StationDTO {
    private long stationCode;
    private String stationName;
    private String addr;
    private String tm;
    private String dmX; // 위도
    private String dmY; // 경도

    private String item;
    private String mangName;
    private String year;

}
