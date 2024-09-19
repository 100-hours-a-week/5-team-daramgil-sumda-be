package com.example.sumda.controller;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.service.AirPollutionImageService;
import com.example.sumda.service.AirQualityService;
import com.example.sumda.utils.ResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/air")
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;
    private final AirPollutionImageService airPollutionImageService;

    // 현재 대기질 정보 조회
    @GetMapping("/current")
    public ResponseEntity<?> getNowAirInfoData(@RequestParam("id") Long id) throws JsonProcessingException {
        // null 값을 오류로 처리하지 않고 그대로 전달
        AirQualityDto airQualityDto = airQualityService.getNowAirQualityData(id);
        return ResponseUtils.createResponse(HttpStatus.OK, "현재 대기질 정보 조회 완료", airQualityDto);
    }

    // 시간별 대기질 정보 조회
    @GetMapping("/time")
    public ResponseEntity<?> getTimeAirInfoData(@RequestParam("id") Long id) throws JsonProcessingException {
        // null 값을 오류로 처리하지 않고 그대로 전달
        List<AirQualityDto> airQualityDtoList = airQualityService.getTimeAirQualityData(id);
        return ResponseUtils.createResponse(HttpStatus.OK, "시간별 대기질 정보 조회 완료", airQualityDtoList);
    }

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
