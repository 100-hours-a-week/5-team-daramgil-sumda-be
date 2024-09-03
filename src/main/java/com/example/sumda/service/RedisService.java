package com.example.sumda.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String LOCATION_AI_HASH = "LocationAiHash";

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String,String,String> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    // LocationName 와 대응해서 해시키를 저장함.
    public void saveLocationHash(String locationName, String hashKey) {
        hashOperations.put(LOCATION_AI_HASH,locationName,hashKey);
    }

    // LocationName 에 해당하는 해시키를 가져옴.
    public String getLocationHash(String locationName) {
        return hashOperations.get(LOCATION_AI_HASH,locationName);
    }

    // LocationName 에 해당하는 해시키를 삭제함.
    public void deleteLocationHash(String locationName) {
        hashOperations.delete(LOCATION_AI_HASH,locationName);
    }
}
