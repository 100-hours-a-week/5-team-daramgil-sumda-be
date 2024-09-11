package com.example.sumda.service.redis;

import com.example.sumda.entity.redis.RedisLocations;
import com.example.sumda.repository.redis.LocationRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisLocationService {
    @Autowired
    private LocationRedisRepository locationRedisRepository;

    // 데이터 저장
    public RedisLocations saveLocation(RedisLocations location) {
        return locationRedisRepository.save(location);
    }

    // ID로 데이터 조회
    public Optional<RedisLocations> findLocationById(Long id) {
        return locationRedisRepository.findById(id);
    }

}
