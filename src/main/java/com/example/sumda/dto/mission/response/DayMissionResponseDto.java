package com.example.sumda.dto.mission.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DayMissionResponseDto {

    private LocalDate date; // 날짜
    private boolean attendance; // 출석 여부
    private boolean checkAir; // 대기오염조회 여부
    private boolean talkWithSquirrel; // 다람쥐와 대화하기 여부
    private boolean quiz; // ox퀴즈 여부

}
