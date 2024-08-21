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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air")
public class AirInfoController {

    private final AirInfoService airInfoService;
    private final TMStdrCrdntService tmStdrCrdntService;
    private final NearbyMsrstnListService nearbyMsrstnListService;

    // 측정소명으로 가장 최근 대기오염 정보 조회 (즐겨찾기)
    @GetMapping("/latest/favorite-location")
    public ResponseEntity<AirInfoReviewDto> getLatestAirInfoData1(@RequestParam("sensitive_group") String sensitiveGroup,
                                                                  @RequestParam("umd_name") String umdName){
            AirInfoReviewDto dto = airInfoService.getLatestAirQualityData(umdName,sensitiveGroup);
            System.out.println(dto);
            return ResponseEntity.ok(dto);

    }

//    @Deprecated
//    // 위도, 경도, 주소로 가장 최근 대기 오염 정보 조회 (현재위치)
//    @GetMapping("/latest/current-location")
//    public ResponseEntity<AirInfoReviewDto> getLatestAirInfoData2(
////            @RequestParam("latitude") double latitude,
////            @RequestParam("longitude") double longitude,
//            @RequestParam("umdName") String umdName) {
//        try {
//            // OO동 정보로 tm 주소 얻기
//            List<TMDTO> tmList = tmStdrCrdntService.getTMStdrCrdnt(umdName);
//
//            if (tmList.isEmpty()) {
//                return ResponseEntity.status(404).body(null); // 'umdName'으로 TM 정보가 없을 때 처리
//            }
//
//            TMDTO tm = tmList.get(0); // 리스트에서 첫 번째 TMDTO 선택 (필요시 조건 추가 가능)
//            Double tmX = tm.getTmX();
//            Double tmY = tm.getTmY();
//
//            // tmX, tmY를 이용해 주변 관측소 찾기
//            List<StationDTO> stationList = nearbyMsrstnListService.getNearbyMsrstnList(tmX, tmY);
//
//            if (stationList.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 주변 관측소가 없을 때 처리
//            }
//
//            StationDTO nearStation = stationList.get(0); // 첫번째 정보가 가장 가까운 관측소 정의
//            String stationName = nearStation.getStationName();
//
//            // 관측소 이름으로 현재 대기 오염 정보 조회
//            AirInfoReviewDto dto = airInfoService.getLatestAirQualityData(stationName);
//            return ResponseEntity.ok(dto);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }


    @Deprecated
    // 측정소명으로 시간별 대기질 정보 조회 (즐겨찾기)
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
