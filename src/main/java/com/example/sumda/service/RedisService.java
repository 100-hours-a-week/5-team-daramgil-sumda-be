package com.example.sumda.service;

import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.entity.AirQualityData;
import com.example.sumda.entity.redis.RedisAirData;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String LOCATION_AI_HASH = "LocationAiHash";

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String,String,String> hashOperations;

    private final ObjectMapper objectMapper;

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

    // 레디스에서 대기질 이미지 정보 가져오기
    public List<String> findByInformCode(String informCode) {
        // Redis의 Set에서 모든 엔트리 키를 가져옴 (예: air_images:5)
        Set<Object> keys = redisTemplate.opsForSet().members("airImages");

        List<String> imageUrls = new ArrayList<>();

        // 각 Set 엔트리 키에 대해 해시 조회
        for (Object key : keys) {
            if (key instanceof String || key instanceof Integer) {
                // "air_images:<key>" 형식으로 변환
                String hashKey = "airImages:" + key;

                // 각 엔트리(예: air_images:5)를 해시로 조회
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);

                // informCode가 일치하는 데이터만 필터링
                if (entries != null && informCode.equals(entries.get("informCode"))) {
                    // imageUrl 값을 리스트에 추가
                    imageUrls.add((String) entries.get("imageUrl"));
                }
            }
        }
        return imageUrls; // 일치하는 모든 imageUrl 반환
    }
}
