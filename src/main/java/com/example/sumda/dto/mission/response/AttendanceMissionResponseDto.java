package com.example.sumda.dto.mission.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceMissionResponseDto {

    private String status;
    private int userAcorns; // 유저 도토리 수 (일일미션 완료 후 받은 토토리 1개 합산한 값)
}
