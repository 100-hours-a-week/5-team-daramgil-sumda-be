package com.example.sumda.service;

import com.example.sumda.entity.Locations;
import com.example.sumda.entity.redis.RedisLocations;
import com.example.sumda.repository.LocationRepository;
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

    // 매일 특정 시간에 질행되는 스케줄링 메서드
    // TODO: 언제 업로드할지 정해야 됨
    @Scheduled(cron = "0 0 0 * * ?") // "초 분 시 일 월 요일" 형식, 여기서는 매일 자정
    public void loadLocationsToRedis() {
        // DB에서 모든 Locations 데이터 조회
        List<Locations> locationList = locationRepository.findAll();

        // Redis에 일괄 저장
        for (Locations locations : locationList) {
            RedisLocations redisLocations = new RedisLocations();
            redisLocations.setId(locations.getId());
            redisLocations.setStationId(locations.getStation().getId());
            locationRedisRepository.save(redisLocations);
        }

        System.out.println("저장한 지역 수: " + locationList.size());
    }
}
