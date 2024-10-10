package com.example.sumda.entity.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(value = "airImages")
public class RedisAirPollutionImages {

    @Id
    private int id;
    private String informCode;
    private String imageUrl;

}