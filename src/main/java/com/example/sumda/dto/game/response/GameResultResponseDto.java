package com.example.sumda.dto.game.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultResponseDto {

    // 지급된 도토리 수 - 10점 당 1개
    private int getAcorns;

    // 유저가 보유 중인 총 도토리 수
    private int userAcorns;
}
