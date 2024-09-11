package com.example.sumda.repository.redis;

import com.example.sumda.entity.redis.RedisAirStation;
import org.springframework.data.repository.CrudRepository;

public interface AirStationsRedisRepository extends CrudRepository<RedisAirStation, Long> {

}
