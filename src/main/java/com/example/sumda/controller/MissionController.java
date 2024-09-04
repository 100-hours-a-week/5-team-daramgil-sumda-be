package com.example.sumda.controller;

import com.example.sumda.dto.mission.response.DayMissionResponseDto;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.MissionService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {

    private final MissionService missionService;

    // 일일 미션 현황 불러오기
    @GetMapping("/day")
    public ResponseEntity<?> getDayMissions(){

        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;

        // 오늘 일일미션 수행 여부(조회시점 기준)
        DayMissionResponseDto dayMissionDto = missionService.getDayMissions(userId);

        return ResponseUtils.createResponse(HttpStatus.OK,"일일미션 현황 조회", dayMissionDto);

    }

    // 일일 미션 출석 완료 처리

    // 일일 미션 OX퀴즈 참여 완료 처리

    // 일일 미션 대기오염조회 완료 처리

    // 일일 미션 다람쥐와 대화하기 완료 처리

}
