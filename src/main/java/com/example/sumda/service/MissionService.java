package com.example.sumda.service;

import com.example.sumda.dto.mission.response.MissionResponseDto;
import com.example.sumda.dto.mission.response.DayMissionResponseDto;
import com.example.sumda.entity.Missions;
import com.example.sumda.entity.User;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.MissionRepository;
import com.example.sumda.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        // 일일미션 여부 확인
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
    @Transactional
    public MissionResponseDto attendanceMission(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 일일미션 여부 확인
        Optional<Missions> optionalDayMission = missionRepository.findByUserIdAndDate(user, LocalDate.now());

        Missions todayMissions;
        if(optionalDayMission.isPresent()) {
            // 이미 존재하는 미션 데이터를 가져오기
            todayMissions = optionalDayMission.get();

            // 출석 미션 완료여부 확인
            if(todayMissions.isAttendance()) { // 출석 여부가 이미 완료이면 도토리 미 지급 - 이미 완료한 미션입니다. 안내
                MissionResponseDto dto = new MissionResponseDto();
                dto.setStatus("ERROR");
                dto.setUserAcorns(user.getUserAcorn());
                return dto;
            } else {
                // 도토리 1개 지급 - 엔티티 설정
                user.setUserAcorn(user.getUserAcorn() + 1);
                todayMissions.setAttendance(true);
            }
        } else {
            // 새로운 미션 데이터 생성
            todayMissions = new Missions();
            todayMissions.setUserId(user);
            todayMissions.setDate(LocalDate.now());
            todayMissions.setAttendance(true);

            // 도토리 1개 지급
            user.setUserAcorn(user.getUserAcorn() + 1);
        }

        // 저장
        userRepository.save(user);
        missionRepository.save(todayMissions);

        MissionResponseDto dto = new MissionResponseDto();
        dto.setStatus("SUCCESS");
        dto.setUserAcorns(user.getUserAcorn());
        return dto;
    }


    // 일일 미션 OX퀴즈 참여 완료 처리
    public MissionResponseDto quizMission(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 일일미션 여부 확인
        Optional<Missions> optionalDayMission = missionRepository.findByUserIdAndDate(user, LocalDate.now());

        Missions todayMissions;
        if(optionalDayMission.isPresent()) {
            // 이미 존재하는 미션 데이터를 가져오기
            todayMissions = optionalDayMission.get();

            // OX퀴즈 미션 완료여부 확인
            if(todayMissions.isQuiz()) { // 퀴즈 여부가 이미 완료이면 도토리 미 지급 - 이미 완료한 미션입니다. 안내
                MissionResponseDto dto = new MissionResponseDto();
                dto.setStatus("ERROR");
                dto.setUserAcorns(user.getUserAcorn());
                return dto;
            } else {
                // 도토리 1개 지급 - 엔티티 설정
                user.setUserAcorn(user.getUserAcorn() + 1);
                todayMissions.setQuiz(true);
            }
        } else {
            // 새로운 미션 데이터 생성
            todayMissions = new Missions();
            todayMissions.setUserId(user);
            todayMissions.setDate(LocalDate.now());
            todayMissions.setQuiz(true);

            // 도토리 1개 지급
            user.setUserAcorn(user.getUserAcorn() + 1);
        }

        // 저장
        userRepository.save(user);
        missionRepository.save(todayMissions);

        MissionResponseDto dto = new MissionResponseDto();
        dto.setStatus("SUCCESS");
        dto.setUserAcorns(user.getUserAcorn());
        return dto;
    }

    // 일일 미션 대기오염조회 완료 처리
    public MissionResponseDto checkAirMission(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 일일미션 여부 확인
        Optional<Missions> optionalDayMission = missionRepository.findByUserIdAndDate(user, LocalDate.now());

        Missions todayMissions;
        if(optionalDayMission.isPresent()) {
            // 이미 존재하는 미션 데이터를 가져오기
            todayMissions = optionalDayMission.get();

            // 출석 미션 완료여부 확인
            if(todayMissions.isCheckAir()) { // 대기오염조회 미션 여부가 이미 완료이면 도토리 미 지급 - 이미 완료한 미션입니다. 안내
                MissionResponseDto dto = new MissionResponseDto();
                dto.setStatus("ERROR");
                dto.setUserAcorns(user.getUserAcorn());
                return dto;
            } else {
                // 도토리 1개 지급 - 엔티티 설정
                user.setUserAcorn(user.getUserAcorn() + 1);
                todayMissions.setCheckAir(true);
            }
        } else {
            // 새로운 미션 데이터 생성
            todayMissions = new Missions();
            todayMissions.setUserId(user);
            todayMissions.setDate(LocalDate.now());
            todayMissions.setCheckAir(true);

            // 도토리 1개 지급
            user.setUserAcorn(user.getUserAcorn() + 1);
        }

        // 저장
        userRepository.save(user);
        missionRepository.save(todayMissions);

        MissionResponseDto dto = new MissionResponseDto();
        dto.setStatus("SUCCESS");
        dto.setUserAcorns(user.getUserAcorn());
        return dto;
    }

    // 일일 미션 다람쥐와 대화하기 완료 처리
    public MissionResponseDto talkWithSquirrelMission(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 일일미션 여부 확인
        Optional<Missions> optionalDayMission = missionRepository.findByUserIdAndDate(user, LocalDate.now());

        Missions todayMissions;
        if(optionalDayMission.isPresent()) {
            // 이미 존재하는 미션 데이터를 가져오기
            todayMissions = optionalDayMission.get();

            // 출석 미션 완료여부 확인
            if(todayMissions.isTalkWithSquirrel()) { // 출석 여부가 이미 완료이면 도토리 미 지급 - 이미 완료한 미션입니다. 안내
                MissionResponseDto dto = new MissionResponseDto();
                dto.setStatus("ERROR");
                dto.setUserAcorns(user.getUserAcorn());
                return dto;
            } else {
                // 도토리 1개 지급 - 엔티티 설정
                user.setUserAcorn(user.getUserAcorn() + 1);
                todayMissions.setTalkWithSquirrel(true);
            }
        } else {
            // 새로운 미션 데이터 생성
            todayMissions = new Missions();
            todayMissions.setUserId(user);
            todayMissions.setDate(LocalDate.now());
            todayMissions.setTalkWithSquirrel(true);

            // 도토리 1개 지급
            user.setUserAcorn(user.getUserAcorn() + 1);
        }

        // 저장
        userRepository.save(user);
        missionRepository.save(todayMissions);

        MissionResponseDto dto = new MissionResponseDto();
        dto.setStatus("SUCCESS");
        dto.setUserAcorns(user.getUserAcorn());
        return dto;
    }
}
