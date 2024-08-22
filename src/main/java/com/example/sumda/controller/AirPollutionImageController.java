package com.example.sumda.controller;

import com.example.sumda.DTO.AirPollutionImageDto;
import com.example.sumda.service.AirPollutionImageService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air")
public class AirPollutionImageController {

    private final AirPollutionImageService airPollutionImageService;

    // 대기질 예측 이미지 조회
    @GetMapping("/image")
    public ResponseEntity<?> getAirPollutionImages(){
        try {
            AirPollutionImageDto airPollutionImageDto = airPollutionImageService.fetchFromPublicApi();
            if (airPollutionImageDto != null) {
                return ResponseUtils.createResponse(HttpStatus.CREATED, "대기질 예측 이미지 조회 완료", airPollutionImageDto);
            } else {
                return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, "데이터가 없습니다.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
