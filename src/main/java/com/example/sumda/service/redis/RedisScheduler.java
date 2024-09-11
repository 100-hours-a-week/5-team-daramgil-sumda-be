package com.example.sumda.service.redis;

import com.example.sumda.entity.AirPollutionImages;
import com.example.sumda.entity.AirQualityStations;
import com.example.sumda.entity.Locations;
import com.example.sumda.entity.redis.RedisAirPollutionImages;
import com.example.sumda.entity.redis.RedisAirStation;
import com.example.sumda.entity.redis.RedisLocations;
import com.example.sumda.repository.AirPollutionImageRepository;
import com.example.sumda.repository.AirQualityStationRepository;
import com.example.sumda.repository.LocationRepository;
import com.example.sumda.repository.redis.AirPollutionImageRedisRepository;
import com.example.sumda.repository.redis.AirStationsRedisRepository;
import com.example.sumda.repository.redis.LocationRedisRepository;
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

    // 매일 특정 시간에 진행되는 스케줄링 메서드 - 지역 정보
    // TODO: 언제 업로드할지 정해야 됨
    @Scheduled(cron = "0 0 0 * * ?") // "초 분 시 일 월 요일" 형식, 여기서는 매일 자정
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
    public void loadAirStationsToRedis(){
        List<AirQualityStations> stationList = airQualityStationRepository.findAll();

        for (AirQualityStations stations : stationList) {
            RedisAirStation redisAirStation = new RedisAirStation();
            redisAirStation.setId(redisAirStation.getId());
            redisAirStation.setStationName(redisAirStation.getStationName());
            airStationsRedisRepository.save(redisAirStation);
        }
    }

    // 대기오염 이미지 저장 (9개)
    // TODO: 업로드 주기 및 시기 추후 결정
    @Scheduled(cron = "0 0 0 * * ?")
    public void airPollutionImageToRedis() {
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

    // 날씨 데이터 저장

    //
}
