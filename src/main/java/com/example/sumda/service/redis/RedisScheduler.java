package com.example.sumda.service.redis;

import com.example.sumda.entity.*;
import com.example.sumda.entity.redis.*;
import com.example.sumda.repository.*;
import com.example.sumda.repository.redis.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisScheduler {

    private final RedisTemplate<String, Object> redisTemplate;

    private final LocationRepository locationRepository;
    private final LocationRedisRepository locationRedisRepository;

    private final AirQualityStationRepository airQualityStationRepository;

    private final AirPollutionImageRedisRepository airPollutionImageRedisRepository;
    private final AirPollutionImageRepository airPollutionImageRepository;

    private final AirQualityDataRepository airQualityDataRepository;
    private final AirDataRedisRepository airDataRedisRepository;

    private final CityWeatherDataRepository cityWeatherDataRepository;
    private final WeatherDataRedisRepository weatherDataRedisRepository;

    // 매일 특정 시간에 진행되는 스케줄링 메서드 - 지역 정보
    // "초 분 시 일 월 요일" 형식
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void loadLocationsToRedis() {
        //DB에서 모든 관측소(AirQualityStations) 데이터를 조회하고 Map으로 변환 (Key: 관측소 ID, Value: 관측소 이름)
        List<AirQualityStations> stationList = airQualityStationRepository.findAll();
        Map<Long, String> stationMap = stationList.stream()
                .collect(Collectors.toMap(AirQualityStations::getId, AirQualityStations::getStationName));

        // DB에서 모든 Locations 데이터 조회
        List<Locations> locationList = locationRepository.findAll();

        // Redis에 location 데이터를 저장하면서, stationId에 해당하는 관측소 이름도 함께 저장
        for (Locations locations : locationList) {
            RedisLocations redisLocations = new RedisLocations();
            redisLocations.setId(locations.getId());
            redisLocations.setDistrict(locations.getDistrict());
            redisLocations.setLatitude(locations.getLatitude());
            redisLocations.setLongitude(locations.getLongitude());
            redisLocations.setStationId(locations.getStation().getId());
            redisLocations.setCityWeatherId(locations.getCityWeatherId());

            // stationId로 관측소 이름을 가져와서 매핑
            Long stationId = locations.getStation().getId();
            String stationName = stationMap.get(stationId);
            if (stationName != null) {
                redisLocations.setStationName(stationName); // 관측소 이름 저장
            } else {
                redisLocations.setStationName("Unknown"); // 관측소를 찾을 수 없는 경우
            }

            // Redis에 저장
            locationRedisRepository.save(redisLocations); // locations:1
        }

        System.out.println("저장한 지역 수: " + locationList.size());
    }

    // 대기오염 이미지 저장 (9개)
    @Transactional
    @Scheduled(cron = "0 15 9,18 * * ?") // 오전 9시 5분, 오후 6시 5분
    public void loadAirPollutionImageToRedis() {
        // DB에서 air pollution image 데이터 조회
        List<AirPollutionImages> airImageList = airPollutionImageRepository.findAll();

        // Redis에 일괄 저장
        for (AirPollutionImages airPollutionImages : airImageList) {
            RedisAirPollutionImages redisAirPollutionImages = new RedisAirPollutionImages();
            redisAirPollutionImages.setId(airPollutionImages.getId());
            redisAirPollutionImages.setInformCode(airPollutionImages.getInformCode());
            redisAirPollutionImages.setImageUrl(airPollutionImages.getImageUrl());
            airPollutionImageRedisRepository.save(redisAirPollutionImages); // airImages:1
        }
    }

    // 대기오염 데이터 저장
    @Transactional
    @Scheduled(cron = "0 15 9,18 * * ?")
    public void loadAirDataToRedis(){
        List<AirQualityData> airDataList = airQualityDataRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화용

        // Redis에 저장
        for (AirQualityData airData : airDataList) {
            RedisAirData redisAirData = new RedisAirData();

            // 각 필드별로 null 체크 및 기본값 설정
            redisAirData.setId(airData.getId());
            redisAirData.setStationName(airData.getStationName() != null ? airData.getStationName() : "null");
            redisAirData.setSo2(airData.getSo2() != null ? airData.getSo2() : 0);
            redisAirData.setCo(airData.getCo() != null ? airData.getCo() : 0);
            redisAirData.setNo2(airData.getNo2() != null ? airData.getNo2() : 0);
            redisAirData.setPm10(airData.getPm10() != null ? airData.getPm10() : 0);
            redisAirData.setPm25(airData.getPm25() != null ? airData.getPm25() : 0);
            redisAirData.setSo2Grade(airData.getSo2Grade() != null ? airData.getSo2Grade() : 0);
            redisAirData.setCoGrade(airData.getCoGrade() != null ? airData.getCoGrade() : 0);
            redisAirData.setO3Grade(airData.getO3Grade() != null ? airData.getO3Grade() : 0);
            redisAirData.setNo2Grade(airData.getNo2Grade() != null ? airData.getNo2Grade() : 0);
            redisAirData.setPm10Grade(airData.getPm10Grade() != null ? airData.getPm10Grade() : 0);
            redisAirData.setPm25Grade(airData.getPm25Grade() != null ? airData.getPm25Grade() : 0);
            redisAirData.setKhaiValue(airData.getKhaiValue() != null ? airData.getKhaiValue() : 0);
            redisAirData.setKhaiGrade(airData.getKhaiGrade() != null ? airData.getKhaiGrade() : 0);
            redisAirData.setDataTime(airData.getDataTime() != null ? String.valueOf(airData.getDataTime()) : "null");

            // Redis에 저장할 키를 "air_data:stationName" 형식으로 생성
            String redisKey = "airData:" + redisAirData.getStationName(); // airData:동홍동

            // Redis에 JSON 형태로 저장
            try {
                String airDataJson = objectMapper.writeValueAsString(redisAirData);
                redisTemplate.opsForHash().put(redisKey, redisAirData.getId().toString(), airDataJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    // 날씨 데이터 저장
    @Transactional
    @Scheduled(cron = "0 14 9,18 * * ?")
    public void loadWeatherDataToRedis(){
        List<CityWeatherData> weatherDataList = cityWeatherDataRepository.findAll();

        for (CityWeatherData cityWeatherData : weatherDataList) {
            RedisWeatherData redisWeatherData = new RedisWeatherData();
            redisWeatherData.setId(cityWeatherData.getId());
            redisWeatherData.setCityOrGun(cityWeatherData.getCityOrGun());
            redisWeatherData.setLatitude(cityWeatherData.getLatitude());
            redisWeatherData.setLongitude(cityWeatherData.getLongitude());

            // JSON 문자열로 변환하여 Redis에 저장
            redisWeatherData.setWeatherDataJson(cityWeatherData.getWeatherData());

            // Redis에 저장
            weatherDataRedisRepository.save(redisWeatherData); // weatherData:1
        }
    }

}
