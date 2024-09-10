package com.example.sumda.entity.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(value = "locations")
public class RedisLocations {

    @Id
    private Long id; // 지역 id
    private Long stationId; // 관측소 id

    // 아큐웨더 api 요청하고 저장하고 있는 데이터 테이블인 city_weather_data 의 Id
    private Long cityWeatherId;
}
