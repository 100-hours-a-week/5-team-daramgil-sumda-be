package com.example.sumda.repository.redis;

import com.example.sumda.entity.redis.RedisAirPollutionImages;
import org.springframework.data.repository.CrudRepository;

public interface AirPollutionImageRedisRepository extends CrudRepository<RedisAirPollutionImages, Integer>{
}
