package com.example.sumda.controller;

import com.example.sumda.DTO.StationDTO;
import com.example.sumda.DTO.TMDTO;
import com.example.sumda.dto.AirInfoReviewDto;
import com.example.sumda.service.AirInfoService;
import com.example.sumda.service.NearbyMsrstnListService;
import com.example.sumda.service.TMStdrCrdntService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    // 측정소명으로 가장 최근 대기오염 정보 조회 (즐겨찾기)
    @GetMapping("/time/favorite-location")
    public ResponseEntity<List<AirInfoReviewDto>> getTimeAirInfoData1(@RequestParam("stationName") String stationName) {
        try {
            // getTimeAirQualityData는 List<AirInfoDTO>를 반환합니다.
            List<AirInfoReviewDto> airInfoList = airInfoService.getTimeAirQualityData(stationName);
            if (!airInfoList.isEmpty()) {
                return ResponseEntity.ok(airInfoList); // 전체 데이터를 반환
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 데이터가 없는 경우 처리
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Deprecated
    // 위도, 경도, 주소로 가장 최근 대기 오염 정보 조회 (현재위치)
    @GetMapping("/time/current-location")
    public ResponseEntity<List<AirInfoReviewDto>> getTimeAirInfoData2(
//            @RequestParam("latitude") double latitude,
//            @RequestParam("longitude") double longitude,
            @RequestParam("umdName") String umdName) {
        try {
            // OO동 정보로 tm 주소 얻기
            List<TMDTO> tmList = tmStdrCrdntService.getTMStdrCrdnt(umdName);

            if (tmList.isEmpty()) {
                return ResponseEntity.status(404).body(null); // 'umdName'으로 TM 정보가 없을 때 처리
            }

            TMDTO tm = tmList.get(0); // 리스트에서 첫 번째 TMDTO 선택 (필요시 조건 추가 가능)
            Double tmX = tm.getTmX();
            Double tmY = tm.getTmY();

            // tmX, tmY를 이용해 주변 관측소 찾기
            List<StationDTO> stationList = nearbyMsrstnListService.getNearbyMsrstnList(tmX, tmY);

            if (stationList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 주변 관측소가 없을 때 처리
            }

            StationDTO nearStation = stationList.get(0); // 첫번째 정보가 가장 가까운 관측소 정의
            String stationName = nearStation.getStationName();

            // 관측소 이름으로 현재 대기 오염 정보 조회
            List<AirInfoReviewDto> airInfoList = airInfoService.getTimeAirQualityData(stationName);

            if (!airInfoList.isEmpty()) {
                return ResponseEntity.ok(airInfoList); // 리스트 전체를 반환(시간대별 정보)
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 데이터가 없을 때 처리
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
