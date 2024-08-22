package com.example.sumda.controller;

import com.example.sumda.dto.AirInfoReviewDto;
import com.example.sumda.dto.AirQualityDto;
import com.example.sumda.service.AirInfoService;
import com.example.sumda.service.NearbyMsrstnListService;
import com.example.sumda.service.TMStdrCrdntService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air")
public class AirInfoController {

    private final AirInfoService airInfoService;
    private final TMStdrCrdntService tmStdrCrdntService;
    private final NearbyMsrstnListService nearbyMsrstnListService;

    // 주소 id 값으로 가장 최근 대기오염 정보 조회
    @GetMapping("/current")
    public ResponseEntity<?> getNowAirInfoData(@RequestParam("id") long id){

        try{

            AirInfoReviewDto dto = airInfoService.getNowAirQualityData(id);
            System.out.println(dto);

            AirQualityDto airQualityDto = new AirQualityDto();
            airQualityDto.setDataTime(dto.getDataTime()); // String 타입으로 변경된 dateTime 설정
            airQualityDto.setKhaiGrade(dto.getKhaiGrade());
            airQualityDto.setKhaiValue(dto.getKhaiValue());
            airQualityDto.setPm10Grade(dto.getPm10Grade());
            airQualityDto.setPm10Value(dto.getPm10Value());
            airQualityDto.setPm25Grade(dto.getPm25Grade());
            airQualityDto.setPm25Value(dto.getPm25Value());
            airQualityDto.setO3Grade(dto.getO3Grade());
            airQualityDto.setO3Value(dto.getO3Value());
            airQualityDto.setNo2Grade(dto.getNo2Grade());
            airQualityDto.setNo2Value(dto.getNo2Value());
            airQualityDto.setCoGrade(dto.getCoGrade());
            airQualityDto.setCoValue(dto.getCoValue());
            airQualityDto.setSo2Grade(dto.getSo2Grade());
            airQualityDto.setSo2Value(dto.getSo2Value());

            if (airQualityDto != null) {
                return ResponseUtils.createResponse(HttpStatus.CREATED, "현재 대기질 정보 조회 완료", airQualityDto);
            } else {
                return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, "데이터가 없습니다.");
            }
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "현재 대기질 정보 조회 실패");
        }
    }


    // 측정소명으로 시간별 대기질 정보 조회 (즐겨찾기)
    @Deprecated
    @GetMapping("/time")
    public ResponseEntity<?> getTimeAirInfoData(@RequestParam("id") long id) {
        try {

            List<AirInfoReviewDto> airInfoList = airInfoService.getTimeAirQualityData(id);

            // AirInfoReviewDto 리스트를 AirQualityDto 리스트로 변환
            List<AirQualityDto> airQualityDtoList = airInfoList.stream().map(airInfo -> {
                AirQualityDto airQualityDto = new AirQualityDto();
                airQualityDto.setDataTime(airInfo.getDataTime()); // String 타입으로 변경된 dateTime 설정
                airQualityDto.setKhaiGrade(airInfo.getKhaiGrade());
                airQualityDto.setKhaiValue(airInfo.getKhaiValue());
                airQualityDto.setPm10Grade(airInfo.getPm10Grade());
                airQualityDto.setPm10Value(airInfo.getPm10Value());
                airQualityDto.setPm25Grade(airInfo.getPm25Grade());
                airQualityDto.setPm25Value(airInfo.getPm25Value());
                airQualityDto.setO3Grade(airInfo.getO3Grade());
                airQualityDto.setO3Value(airInfo.getO3Value());
                airQualityDto.setNo2Grade(airInfo.getNo2Grade());
                airQualityDto.setNo2Value(airInfo.getNo2Value());
                airQualityDto.setCoGrade(airInfo.getCoGrade());
                airQualityDto.setCoValue(airInfo.getCoValue());
                airQualityDto.setSo2Grade(airInfo.getSo2Grade());
                airQualityDto.setSo2Value(airInfo.getSo2Value());
                return airQualityDto;
            }).collect(Collectors.toList());

            if (!airQualityDtoList.isEmpty()) {
                return ResponseUtils.createResponse(HttpStatus.CREATED, "시간대별 대기질 정보 조회 완료", airQualityDtoList);
            } else {
                return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, "데이터가 없습니다.");
            }
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "시간대별 대기질 정보 조회 실패");
        }
    }

}
