package com.example.sumda.controller;

import com.example.sumda.dto.squirrel.request.AcronRequestDto;
import com.example.sumda.dto.squirrel.request.NewSquirrelRequestDto;
import com.example.sumda.dto.squirrel.response.FeedSquirrelResponseDto;
import com.example.sumda.dto.squirrel.response.NewSquirrelResponseDto;
import com.example.sumda.dto.squirrel.response.UserSquirrelResponseDto;
import com.example.sumda.entity.UserSquirrel;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.SquirrelService;
import com.example.sumda.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/squirrel")
public class SquirrelController {

    @Autowired
    private SquirrelService squirrelService;

    @GetMapping("/")
    // 유저의 다람쥐 정보 불러오기
    public ResponseEntity<?> getUserSquirrel() {
        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;
        UserSquirrelResponseDto userSquirrelResponseDto = squirrelService.getUserSquirrel(userId);

        if(userSquirrelResponseDto == null) {
            throw new CustomException(ErrorCode.USER_SQUIRREL_NOT_FOUND);
        }
            return ResponseUtils.createResponse(HttpStatus.OK,"현재 키우고 있는 다람쥐 정보 조회 완료", userSquirrelResponseDto);
    }

    @PostMapping("/feed")
    // 다람쥐한테 도토리 주기 (개수 유저가 지정)
    public ResponseEntity<?> feedSquirrelOnAcorns(@RequestBody AcronRequestDto acornRequest) {
        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;
        int acorns = acornRequest.getAcorns(); // 다람쥐한테 줄 도토리 개수 가져오기

        FeedSquirrelResponseDto feedSquirrelDto = squirrelService.feedSquirrelOnAcorns(userId, acorns);

        return ResponseUtils.createResponse(HttpStatus.OK,"다람쥐한테 도토리 주기 완료", feedSquirrelDto);
    }

    @PostMapping("/new")
    // 새로운 다람쥐 분양 받기
     public ResponseEntity<?> newSquirrel(@RequestBody NewSquirrelRequestDto newSquirrelRequestDto) {

        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;
        String sqrType = newSquirrelRequestDto.getSqrType();

        NewSquirrelResponseDto newSquirrelResponseDto = squirrelService.getNewSquirrel(userId, sqrType);

        return ResponseUtils.createResponse(HttpStatus.OK, "다람쥐 분양 완료", newSquirrelResponseDto);

    }

    // 다람쥐 컬렉션
}
