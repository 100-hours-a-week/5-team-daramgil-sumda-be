package com.example.sumda.dto.game.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HighestScoreResponseDto {

    private Long gameTypeId; // 게임 종류
    private int highestScore; // 본인 최고 점수
}
