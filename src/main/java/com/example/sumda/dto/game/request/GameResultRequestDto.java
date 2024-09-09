package com.example.sumda.dto.game.request;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class GameResultRequestDto {

    private Long gameId; // 게임 종류 아이디
    private Timestamp startTime; // 게임 시작 시간
    private int score; // 게임 점수(현재 점수)

}
