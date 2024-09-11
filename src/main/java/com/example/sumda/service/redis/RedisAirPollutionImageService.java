package com.example.sumda.service.redis;

import com.example.sumda.entity.redis.RedisAirPollutionImages;
import com.example.sumda.repository.redis.AirPollutionImageRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisAirPollutionImageService {

    @Autowired
    private AirPollutionImageRedisRepository airPollutionImageRedisRepository;

    public RedisAirPollutionImages saveAirPollutionImages (RedisAirPollutionImages airPollutionImages){
        return airPollutionImageRedisRepository.save(airPollutionImages);
    }
}
