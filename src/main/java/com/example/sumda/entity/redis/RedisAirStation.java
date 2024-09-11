package com.example.sumda.entity.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(value = "air_station")
public class RedisAirStation {

    @Id
    private Long id;

    private String stationName;
}
