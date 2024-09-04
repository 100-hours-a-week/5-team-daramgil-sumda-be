package com.example.sumda.controller;

import com.example.sumda.dto.mission.response.AttendanceMissionResponseDto;
import com.example.sumda.dto.mission.response.DayMissionResponseDto;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.service.MissionService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {

    private final MissionService missionService;

    // 일일 미션 현황 불러오기
    @GetMapping("/day")
    public ResponseEntity<?> getDayMissions() {

        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;

        // 오늘 일일미션 수행 여부(조회시점 기준)
        DayMissionResponseDto dayMissionDto = missionService.getDayMissions(userId);

        return ResponseUtils.createResponse(HttpStatus.OK,"일일미션 현황 조회", dayMissionDto);

    }

    // 일일 미션 출석 완료 처리
    @PostMapping("/attendance")
    public ResponseEntity<?> AttendanceSuccess() {
        // TODO: jwt에서 userId 가져오기
        Long userId = 1L;

        // 출석 완료 처리
        AttendanceMissionResponseDto dto = missionService.attendanceMission(userId);

        if(dto.getStatus() == "ERROR"){
            return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST,"이미 완료된 미션입니다.", dto);
        }

        return ResponseUtils.createResponse(HttpStatus.OK,"출석 미션을 완료했습니다. 도토리 1개가 지급됩니다.", dto);
    }

    // 일일 미션 OX퀴즈 참여 완료 처리

    // 일일 미션 대기오염조회 완료 처리

    // 일일 미션 다람쥐와 대화하기 완료 처리

}
