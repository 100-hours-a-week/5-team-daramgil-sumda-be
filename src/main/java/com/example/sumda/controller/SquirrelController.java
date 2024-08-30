package com.example.sumda.controller;

import com.example.sumda.dto.squirrel.response.FeedSquirrelResponseDto;
import com.example.sumda.dto.squirrel.response.UserSquirrelResponseDto;
import com.example.sumda.entity.UserSquirrel;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.SquirrelService;
import com.example.sumda.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/squirrel")
public class SquirrelController {

    private SquirrelService squirrelService;

    @GetMapping("/")
    // 유저의 다람쥐 정보 불러오기
    public ResponseEntity<?> getUserSquirrel(@RequestParam("userId") Long userId) {
        UserSquirrelResponseDto userSquirrelResponseDto = squirrelService.getUserSquirrel(userId);

        if(userSquirrelResponseDto == null) {
            throw new CustomException(ErrorCode.USER_SQUIRREL_NOT_FOUND);
        }
            return ResponseUtils.createResponse(HttpStatus.OK,"현재 키우고 있는 다람쥐 정보 조회 완료", userSquirrelResponseDto);
    }

    // 다람쥐한테 도토리 주기 (개수 유저가 지정)
    public ResponseEntity<?> feedSquirrelOnAcorns(@RequestParam("acorns") int acorns) {
        Long userId = 1L;
        FeedSquirrelResponseDto feedSquirrelDto = squirrelService.feedSquirrelOnAcorns(userId, acorns);

        return ResponseUtils.createResponse(HttpStatus.OK,"다람쥐한테 도토리 주기 완료", feedSquirrelDto);
    }

    // 새로운 다람쥐 분양 받기

    // 다람쥐 컬렉션
}
