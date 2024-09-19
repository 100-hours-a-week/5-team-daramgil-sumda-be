package com.example.sumda.service;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.entity.AirPollutionImages;
import com.example.sumda.entity.AirQualityData;
import com.example.sumda.entity.AirQualityStations;
import com.example.sumda.entity.Locations;
import com.example.sumda.entity.redis.RedisAirPollutionImages;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.AirPollutionImageRepository;
import com.example.sumda.repository.AirQualityDataRepository;
import com.example.sumda.repository.AirQualityStationRepository;
import com.example.sumda.repository.LocationRepository;
import com.example.sumda.repository.redis.AirPollutionImageRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final AirQualityStationRepository airQualityStationRepository;
    private final AirQualityDataRepository airQualityDataRepository;
    private final LocationRepository locationRepository;
    private final AirPollutionImageRepository airPollutionImageRepository;
    private final AirPollutionImageRedisRepository airPollutionImageRedisRepository;


    // 측정소별 실시간 측정정보 - 대기질
    public AirQualityDto getNowAirQualityData(long id) {
        // 대기질 정보 불러오기
       List<AirQualityData> airQualityDataList = getAirQualityData(id);

        AirQualityData airQuality = airQualityDataList.get(9); // 가장 최근 데이터

        AirQualityDto airQualityDto = new AirQualityDto();
        airQualityDto.setId(airQuality.getId());
        airQualityDto.setStation_name(airQuality.getStationName());
        airQualityDto.setSo2(String.valueOf(airQuality.getSo2()));
        airQualityDto.setCo(String.valueOf(airQuality.getCo()));
        airQualityDto.setO3(String.valueOf(airQuality.getO3()));
        airQualityDto.setNo2(String.valueOf(airQuality.getNo2()));
        airQualityDto.setPm10(String.valueOf(airQuality.getPm10()));
        airQualityDto.setPm25(String.valueOf(airQuality.getPm25()));
        airQualityDto.setSo2Grade(String.valueOf(airQuality.getSo2Grade()));
        airQualityDto.setCoGrade(String.valueOf(airQuality.getCoGrade()));
        airQualityDto.setO3Grade(String.valueOf(airQuality.getO3Grade()));
        airQualityDto.setNo2Grade(String.valueOf(airQuality.getNo2Grade()));
        airQualityDto.setPm10Grade(String.valueOf(airQuality.getPm10Grade()));
        airQualityDto.setPm25Grade(String.valueOf(airQuality.getPm25Grade()));
        airQualityDto.setKhaiValue(String.valueOf(airQuality.getKhaiValue()));
        airQualityDto.setKhaiGrade(String.valueOf(airQuality.getKhaiGrade()));
        airQualityDto.setDataTime(airQuality.getDataTime());

        return airQualityDto;
    }

    // 시간별 대기질 정보 조회
    public List<AirQualityDto> getTimeAirQualityData(long id) {
        // 대기질 정보 불러오기
        List<AirQualityData> airQualityDataList = getAirQualityData(id);

        // List<AirQualityDto>로 변환
        List<AirQualityDto> airQualityDtoList = airQualityDataList.stream().map(airQuality -> {
            AirQualityDto airQualityDto = new AirQualityDto();
            airQualityDto.setId(airQuality.getId());
            airQualityDto.setStation_name(airQuality.getStationName());
            airQualityDto.setSo2(String.valueOf(airQuality.getSo2()));
            airQualityDto.setCo(String.valueOf(airQuality.getCo()));
            airQualityDto.setO3(String.valueOf(airQuality.getO3()));
            airQualityDto.setNo2(String.valueOf(airQuality.getNo2()));
            airQualityDto.setPm10(String.valueOf(airQuality.getPm10()));
            airQualityDto.setPm25(String.valueOf(airQuality.getPm25()));
            airQualityDto.setSo2Grade(String.valueOf(airQuality.getSo2Grade()));
            airQualityDto.setCoGrade(String.valueOf(airQuality.getCoGrade()));
            airQualityDto.setO3Grade(String.valueOf(airQuality.getO3Grade()));
            airQualityDto.setNo2Grade(String.valueOf(airQuality.getNo2Grade()));
            airQualityDto.setPm10Grade(String.valueOf(airQuality.getPm10Grade()));
            airQualityDto.setPm25Grade(String.valueOf(airQuality.getPm25Grade()));
            airQualityDto.setKhaiValue(String.valueOf(airQuality.getKhaiValue()));
            airQualityDto.setKhaiGrade(String.valueOf(airQuality.getKhaiGrade()));
            airQualityDto.setDataTime(airQuality.getDataTime());
            return airQualityDto;
        }).collect(Collectors.toList());

        return airQualityDtoList;
    }


    // 대기질 예측 이미지 조회
//    public AirPollutionImageResponseDto getAirPollutionImage() {
//
//        List<AirPollutionImages> pm10Images = airPollutionImageRepository.findByInformCode("PM10");
//        List<AirPollutionImages> pm25Images = airPollutionImageRepository.findByInformCode("PM25");
//        List<AirPollutionImages> o3Images = airPollutionImageRepository.findByInformCode("O3");
//
//        AirPollutionImageResponseDto airPollutionImagesDto = new AirPollutionImageResponseDto();
//
//        // 각 informCode에 해당하는 이미지를 AirPollutionImage로 변환하여 리스트에 추가
//        List<AirPollutionImageResponseDto.AirPollutionImage> airPollutionImagesList = new ArrayList<>();
//
//        // PM10 이미지 설정
//        AirPollutionImageResponseDto.AirPollutionImage pm10ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
//        pm10ImageDto.setInformCode("PM10");
//        pm10ImageDto.setImages(pm10Images.stream()
//                .map(AirPollutionImages::getImageUrl) // AirPollutionImages 객체에서 이미지 URL 추출
//                .collect(Collectors.toList()));
//        airPollutionImagesList.add(pm10ImageDto);
//
//        // PM25 이미지 설정
//        AirPollutionImageResponseDto.AirPollutionImage pm25ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
//        pm25ImageDto.setInformCode("PM25");
//        pm25ImageDto.setImages(pm25Images.stream()
//                .map(AirPollutionImages::getImageUrl)
//                .collect(Collectors.toList()));
//        airPollutionImagesList.add(pm25ImageDto);
//
//        // O3 이미지 설정
//        AirPollutionImageResponseDto.AirPollutionImage o3ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
//        o3ImageDto.setInformCode("O3");
//        o3ImageDto.setImages(o3Images.stream()
//                .map(AirPollutionImages::getImageUrl)
//                .collect(Collectors.toList()));
//        airPollutionImagesList.add(o3ImageDto);
//
//        // 최종 DTO에 설정
//        airPollutionImagesDto.setAirPollutionImages(airPollutionImagesList);
//
//        return airPollutionImagesDto;
//    }

    public List<AirQualityData> getAirQualityData(long id) {
        // 주소 id로 관측소 id 가져오기
        Locations location = locationRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.LOCATION_ERROR));
        Long stationId = location.getStation().getId();
        System.out.println(stationId);

        // 관측소명 가져오기
        AirQualityStations station = airQualityStationRepository.findById(stationId)
                .orElseThrow(()->new CustomException(ErrorCode.STATION_ERROR));
        String stationName = station.getStationName();
        System.out.println("Station Name: " + stationName);

        // "0" 날짜 값을 NOW()로 업데이트
        airQualityDataRepository.updateZeroDateValues(stationName);

        // 해당 관측소 대기질 정보 조회
        List<AirQualityData> airQualityDataList = airQualityDataRepository.findByStationName(stationName);
        System.out.println(airQualityDataList);

        // 대기질 정보가 존재하지 않을 경우
        if (airQualityDataList.isEmpty()) {
            throw new CustomException(ErrorCode.AIR_INFO_NOT_FOUND);
        }

        return airQualityDataList;
    }


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<String> findByInformCode(String informCode) {
        // Redis의 Set에서 모든 엔트리 키를 가져옴 (예: air_images:5)
        Set<Object> keys = redisTemplate.opsForSet().members("air_images");

        // 로그: Set 키 출력
        System.out.println("Set keys: " + keys);

        List<String> imageUrls = new ArrayList<>();

        // 각 Set 엔트리 키에 대해 해시 조회
        for (Object key : keys) {
            if (key instanceof String || key instanceof Integer) {
                // "air_images:<key>" 형식으로 변환
                String hashKey = "air_images:" + key;

                // 로그: 변환된 키 출력
                System.out.println("Processing hash key: " + hashKey);

                // 각 엔트리(예: air_images:5)를 해시로 조회
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);

                // 로그: 해시 엔트리 출력
                System.out.println("Hash entries for key " + hashKey + ": " + entries);

                // informCode가 일치하는 데이터만 필터링
                if (entries != null && informCode.equals(entries.get("informCode"))) {
                    // imageUrl 값을 리스트에 추가
                    System.out.println("Matching informCode found: " + informCode + ", imageUrl: " + entries.get("imageUrl"));
                    imageUrls.add((String) entries.get("imageUrl"));
                }
            }
        }

        // 로그: 최종 imageUrls 리스트 출력
        System.out.println("Final imageUrls list: " + imageUrls);


        return imageUrls; // 일치하는 모든 imageUrl 반환
    }

    // 대기질 예측 이미지 조회
    // 레디스 사용 테스트
    public AirPollutionImageResponseDto getAirPollutionImage() {

        // 레디스에서 값 가져오기
        List<String> pm10Images = findByInformCode("PM10");
        List<String> pm25Images = findByInformCode("PM25");
        List<String> o3Images = findByInformCode("O3");

        AirPollutionImageResponseDto airPollutionImagesDto = new AirPollutionImageResponseDto();

        // 각 informCode에 해당하는 이미지를 AirPollutionImage로 변환하여 리스트에 추가
        List<AirPollutionImageResponseDto.AirPollutionImage> airPollutionImagesList = new ArrayList<>();

        // PM10 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage pm10ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm10ImageDto.setInformCode("PM10");
        pm10ImageDto.setImages(pm10Images);
        airPollutionImagesList.add(pm10ImageDto);

        // PM25 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage pm25ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm25ImageDto.setInformCode("PM25");
        pm25ImageDto.setImages(pm25Images);
        airPollutionImagesList.add(pm25ImageDto);

        // O3 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage o3ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        o3ImageDto.setInformCode("O3");
        o3ImageDto.setImages(o3Images);
        airPollutionImagesList.add(o3ImageDto);

        // 최종 DTO에 설정
        airPollutionImagesDto.setAirPollutionImages(airPollutionImagesList);

        return airPollutionImagesDto;
    }
}
