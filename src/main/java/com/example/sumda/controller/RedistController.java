package com.example.sumda.controller;

import com.example.sumda.service.redis.RedisScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedistController {

    @Autowired
    private RedisScheduler redisScheduler;

    @GetMapping("/load-air-images")
    public String loadAirPollutionImagesToRedis() {
        redisScheduler.loadAirPollutionImageToRedis();
        return "대기오염 이미지 레디스 저장 완료.";
    }
}
