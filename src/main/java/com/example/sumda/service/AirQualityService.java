package com.example.sumda.service;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.entity.AirPollutionImages;
import com.example.sumda.entity.AirQualityData;
import com.example.sumda.entity.AirQualityStations;
import com.example.sumda.entity.Locations;
import com.example.sumda.entity.redis.RedisAirData;
import com.example.sumda.entity.redis.RedisAirPollutionImages;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.AirPollutionImageRepository;
import com.example.sumda.repository.AirQualityDataRepository;
import com.example.sumda.repository.AirQualityStationRepository;
import com.example.sumda.repository.LocationRepository;
import com.example.sumda.repository.redis.AirPollutionImageRedisRepository;
import com.example.sumda.service.redis.RedisScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScheduler redisScheduler;


    // 측정소별 실시간 측정정보 - 대기질
    public AirQualityDto getNowAirQualityData(long locationId) throws JsonProcessingException {
        // 대기질 정보 불러오기
       List<AirQualityData> airQualityDataList = getAirQualityData(locationId);

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
    public List<AirQualityDto> getTimeAirQualityData(long locationId) throws JsonProcessingException {
        // 대기질 정보 불러오기
        List<AirQualityData> airQualityDataList = getAirQualityData(locationId);

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

    // 주소 id로 대기질 정보 가져오기 (레디스 사용)
    public List<AirQualityData> getAirQualityData(long locationId) throws JsonProcessingException {
        // 주소 id로 관측소명 가져오기
        String redisKey = "locations:" + locationId;
        Map<Object, Object> locationMap = redisTemplate.opsForHash().entries(redisKey);

        String stationName;
        // 레디스에 데이터가 없을 경우 DB에서 조회
        if (locationMap.isEmpty()) {
            // 주소 id로 관측소 id 가져오기
            Locations location = locationRepository.findById(locationId)
                    .orElseThrow(()->new CustomException(ErrorCode.LOCATION_ERROR));
            Long stationId = location.getStation().getId();
//            System.out.println(stationId);

            // 관측소명 가져오기
            AirQualityStations station = airQualityStationRepository.findById(stationId)
                    .orElseThrow(()->new CustomException(ErrorCode.STATION_ERROR));
            stationName = station.getStationName();
            System.out.println("Station Name: " + stationName);

            // Redis에 저장해두기
            redisTemplate.opsForHash().put(redisKey, "stationName", stationName);
        } else {
            stationName = locationMap.get("stationName").toString();
        }

        // 대기질 정보 가져오기
        String airRedisKey = "airData:" + stationName;
        Map<Object, Object> airDataMap = redisTemplate.opsForHash().entries(airRedisKey);
        List<AirQualityData> airQualityDataList = new ArrayList<>();

        // 레디스에 대기질 정보가 없는 경우 RDS 조회
        if (airDataMap.isEmpty()) {
            airQualityDataList = getAirQualityDataFromDB(locationId);

            // RDS에서 가져온 데이터를 Redis에 저장
            for (AirQualityData airQualityData : airQualityDataList) {
                String airDataJson = objectMapper.writeValueAsString(airQualityData);
                redisTemplate.opsForHash().put(airRedisKey, airQualityData.getId().toString(), airDataJson);
            }
        } else {
            // Redis에서 가져온 데이터를 AirQualityData 객체로 변환
            for (Object value : airDataMap.values()) {
                String airDataJson = (String) value;

                // JSON 데이터를 AirQualityData 객체로 변환
                AirQualityData airQualityData = objectMapper.readValue(airDataJson, AirQualityData.class);

                // 리스트에 추가
                airQualityDataList.add(airQualityData);
            }
        }

        return airQualityDataList;
    }

    // 대기질 예측 이미지 조회 (레디스 사용)
    public AirPollutionImageResponseDto getAirPollutionImage() {
        AirPollutionImageResponseDto airPollutionImagesDto = new AirPollutionImageResponseDto();

        // 각 informCode에 해당하는 이미지를 AirPollutionImage로 변환하여 리스트에 추가
        List<AirPollutionImageResponseDto.AirPollutionImage> airPollutionImagesList = new ArrayList<>();

        // PM10 이미지 설정
        List<String> pm10Images = redisService.findByInformCode("PM10");
        if (pm10Images.isEmpty()) {
            // 레디스에 데이터가 없으면 RDS에서 조회
            List<AirPollutionImages> pm10ImagesFromDB = airPollutionImageRepository.findByInformCode("PM10");
            pm10Images = pm10ImagesFromDB.stream()
                    .map(AirPollutionImages::getImageUrl)
                    .collect(Collectors.toList());
        }
        AirPollutionImageResponseDto.AirPollutionImage pm10ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm10ImageDto.setInformCode("PM10");
        pm10ImageDto.setImages(pm10Images);
        airPollutionImagesList.add(pm10ImageDto);

        // PM25 이미지 설정
        List<String> pm25Images = redisService.findByInformCode("PM25");
        if (pm25Images.isEmpty()) {
            // 레디스에 데이터가 없으면 RDS에서 조회
            List<AirPollutionImages> pm25ImagesFromDB = airPollutionImageRepository.findByInformCode("PM25");
            pm25Images = pm25ImagesFromDB.stream()
                    .map(AirPollutionImages::getImageUrl)
                    .collect(Collectors.toList());
        }
        AirPollutionImageResponseDto.AirPollutionImage pm25ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm25ImageDto.setInformCode("PM25");
        pm25ImageDto.setImages(pm25Images);
        airPollutionImagesList.add(pm25ImageDto);

        // O3 이미지 설정
        List<String> o3Images = redisService.findByInformCode("O3");
        if (o3Images.isEmpty()) {
            // 레디스에 데이터가 없으면 RDS에서 조회
            List<AirPollutionImages> o3ImagesFromDB = airPollutionImageRepository.findByInformCode("O3");
            o3Images = o3ImagesFromDB.stream()
                    .map(AirPollutionImages::getImageUrl)
                    .collect(Collectors.toList());
        }
        AirPollutionImageResponseDto.AirPollutionImage o3ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        o3ImageDto.setInformCode("O3");
        o3ImageDto.setImages(o3Images);
        airPollutionImagesList.add(o3ImageDto);

        redisScheduler.loadAirPollutionImageToRedis(); // 레디스에 이미지 저장

        // 최종 DTO에 설정
        airPollutionImagesDto.setAirPollutionImages(airPollutionImagesList);

        return airPollutionImagesDto;
    }

    // RDS 사용
    public List<AirQualityData> getAirQualityDataFromDB(long id) {
        // 주소 id로 관측소 id 가져오기
        Locations location = locationRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.LOCATION_ERROR));
        Long stationId = location.getStation().getId();
//        System.out.println(stationId);

        // 관측소명 가져오기
        AirQualityStations station = airQualityStationRepository.findById(stationId)
                .orElseThrow(()->new CustomException(ErrorCode.STATION_ERROR));
        String stationName = station.getStationName();
//        System.out.println("Station Name: " + stationName);

        // "0" 날짜 값을 NOW()로 업데이트
        airQualityDataRepository.updateZeroDateValues(stationName);

        // 해당 관측소 대기질 정보 조회
        List<AirQualityData> airQualityDataList = airQualityDataRepository.findByStationName(stationName);
//        System.out.println(airQualityDataList);

        // 대기질 정보가 존재하지 않을 경우
        if (airQualityDataList.isEmpty()) {
            throw new CustomException(ErrorCode.AIR_INFO_NOT_FOUND);
        }

        return airQualityDataList;
    }
}
