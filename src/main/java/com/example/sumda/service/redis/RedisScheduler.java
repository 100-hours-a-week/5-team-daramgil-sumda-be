package com.example.sumda.service.redis;

import com.example.sumda.entity.*;
import com.example.sumda.entity.redis.*;
import com.example.sumda.repository.*;
import com.example.sumda.repository.redis.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisScheduler {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private LocationRedisRepository locationRedisRepository;

    @Autowired
    private AirQualityStationRepository airQualityStationRepository;
    @Autowired
    private AirStationsRedisRepository airStationsRedisRepository;

    @Autowired
    private AirPollutionImageRedisRepository airPollutionImageRedisRepository;
    @Autowired
    private AirPollutionImageRepository airPollutionImageRepository;

    @Autowired
    private AirQualityDataRepository airQualityDataRepository;
    @Autowired
    private AirDataRedisRepository airDataRedisRepository;

    @Autowired
    private CityWeatherDataRepository cityWeatherDataRepository;
    @Autowired
    private WeatherDataRedisRepository weatherDataRedisRepository;
    @Autowired
    private ObjectMapper objectMapper;

    // 매일 특정 시간에 진행되는 스케줄링 메서드 - 지역 정보
    // "초 분 시 일 월 요일" 형식
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void loadLocationsToRedis() {
        // DB에서 모든 Locations 데이터 조회
        List<Locations> locationList = locationRepository.findAll();

        // Redis에 일괄 저장 (약 5,000갸)
        for (Locations locations : locationList) {
            RedisLocations redisLocations = new RedisLocations();
            redisLocations.setId(locations.getId());
            redisLocations.setDistrict(locations.getDistrict());
            redisLocations.setLatitude(locations.getLatitude());
            redisLocations.setLongitude(locations.getLongitude());
            redisLocations.setStationId(locations.getStation().getId());
            redisLocations.setCityWeatherId(locations.getCityWeatherId());
            locationRedisRepository.save(redisLocations);
        }

        System.out.println("저장한 지역 수: " + locationList.size());
    }

    // 관측소 정보 저장 (약 660개)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void loadAirStationsToRedis(){
        List<AirQualityStations> stationList = airQualityStationRepository.findAll();

        for (AirQualityStations stations : stationList) {
            RedisAirStation redisAirStation = new RedisAirStation();
            redisAirStation.setId(stations.getId());
            redisAirStation.setStationName(stations.getStationName());
            airStationsRedisRepository.save(redisAirStation);
        }
    }

    // 대기오염 이미지 저장 (9개)
    @Transactional
    @Scheduled(cron = "0 5 9,18 * * ?") // 오전 9시 5분, 오후 6시 5분
    public void loadAirPollutionImageToRedis() {
        // DB에서 air pollution image 데이터 조회
        List<AirPollutionImages> airImageList = airPollutionImageRepository.findAll();

        // Redis에 일괄 저장
        for (AirPollutionImages airPollutionImages : airImageList) {
            RedisAirPollutionImages redisAirPollutionImages = new RedisAirPollutionImages();
            redisAirPollutionImages.setId(airPollutionImages.getId());
            redisAirPollutionImages.setInformCode(airPollutionImages.getInformCode());
            redisAirPollutionImages.setImageUrl(airPollutionImages.getImageUrl());
            airPollutionImageRedisRepository.save(redisAirPollutionImages);
        }
    }

    // 대기오염 데이터 저장
    @Transactional
    @Scheduled(cron = "0 5 9,18 * * ?") // 오전 9시 5분, 오후 6시 5분
    public void loadAirDataToRedis(){
        List<AirQualityData> airDataList = airQualityDataRepository.findAll();

        // Redis에 저장
        for (AirQualityData airData : airDataList) {
            RedisAirData redisAirData = new RedisAirData();
            redisAirData.setId(airData.getId());
            redisAirData.setStationName(airData.getStationName());
            redisAirData.setSo2(airData.getSo2());
            redisAirData.setCo(airData.getCo());
            redisAirData.setNo2(airData.getNo2());
            redisAirData.setPm10(airData.getPm10());
            redisAirData.setPm25(airData.getPm25());
            redisAirData.setSo2Grade(airData.getSo2Grade());
            redisAirData.setCoGrade(airData.getCoGrade());
            redisAirData.setO3Grade(airData.getO3Grade());
            redisAirData.setNo2Grade(airData.getNo2Grade());
            redisAirData.setPm10Grade(airData.getPm10Grade());
            redisAirData.setPm25Grade(airData.getPm25Grade());
            redisAirData.setKhaiValue(airData.getKhaiValue());
            redisAirData.setKhaiGrade(airData.getKhaiGrade());
            redisAirData.setDataTime(airData.getDataTime());
            airDataRedisRepository.save(redisAirData);
        }
    }

    // 날씨 데이터 저장
    @Transactional
    @Scheduled(cron = "0 4 9,18 * * ?") // 오전 9시 4분, 오후 6시 4분
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
            weatherDataRedisRepository.save(redisWeatherData);
        }
    }

}
