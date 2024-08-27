package com.example.sumda.controller;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.service.AirPollutionImageService;
import com.example.sumda.service.AirQualityService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/air")
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;
    private final AirPollutionImageService airPollutionImageService;


    // 현재 대기질 정보 조회
    @GetMapping("/current")
    public ResponseEntity<?> getNowAirInfoData(@RequestParam("id") Long id) {

        AirQualityDto airQualityDto = airQualityService.getNowAirQualityData(id);

        if(airQualityDto != null) {
            return ResponseUtils.createResponse(HttpStatus.OK, "현재 대기질 정보 조회 완료", airQualityDto);
        } else {
            return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, "데이터가 없습니다.");
        }
    }

    //TODO: DB에 시간대별 대기질 정보가 없음
    // 시간별 대기질 정보 조회
//    @GetMapping("/time")
//    public ResponseEntity<?> getTimeAirInfoData(@RequestParam("id") Long id) {
//
//    }


    // 대기질 예측 이미지 조회
    @GetMapping("/image")
    public ResponseEntity<?> getAirPollutionImages(){
        try {
            AirPollutionImageResponseDto airPollutionImagesDto = airQualityService.getAirPollutionImage();
            if (airPollutionImagesDto != null) {
                return ResponseUtils.createResponse(HttpStatus.OK, "대기질 예측 이미지 조회 완료", airPollutionImagesDto);
            } else {
                return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, "데이터가 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
