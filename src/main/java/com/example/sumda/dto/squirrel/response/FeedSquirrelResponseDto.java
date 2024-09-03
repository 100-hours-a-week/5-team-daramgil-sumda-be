package com.example.sumda.dto.squirrel.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedSquirrelResponseDto {

    private int userAcorns; // 유저의 보유 도토리 수
    private String type; // 다람쥐 종류
    private int level; // 다람쥐 레벨
    private int ateAcorns; // 지금까지 먹은 도토리 총 개수
}
