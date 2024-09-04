package com.example.sumda.service;

import com.example.sumda.dto.mission.response.DayMissionResponseDto;
import com.example.sumda.entity.Missions;
import com.example.sumda.entity.User;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.MissionRepository;
import com.example.sumda.repository.UserRepository;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    // 일일 미션 현황 불러오기(조회시점 기준)
    public DayMissionResponseDto getDayMissions(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Optional<Missions> dayMission = missionRepository.findByUserIdAndDate(user, LocalDate.now());
        if(dayMission.isEmpty()) {
            throw new CustomException(ErrorCode.DAY_MISSION_INFO_NOT_FOUND);
        }

        DayMissionResponseDto dayMissionDto = new DayMissionResponseDto();
        dayMissionDto.setDate(dayMission.get().getDate());
        dayMissionDto.setAttendance(dayMission.get().isAttendance());
        dayMissionDto.setCheckAir(dayMission.get().isCheckAir());
        dayMissionDto.setTalkWithSquirrel(dayMission.get().isTalkWithSquirrel());
        dayMissionDto.setQuiz(dayMission.get().isQuiz());

        return dayMissionDto;

    }


    // 일일 미션 출석 완료 처리

    // 일일 미션 OX퀴즈 참여 완료 처리

    // 일일 미션 대기오염조회 완료 처리

    // 일일 미션 다람쥐와 대화하기 완료 처리
}
