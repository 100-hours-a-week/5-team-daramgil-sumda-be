package com.example.sumda.dto.squirrel.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSquirrelResponseDto {

    private Long squirrelId; // 다람쥐 id
    private String type; // 다람쥐 종류
    private int level; // 다람쥐 레벨
    private int feed; // 지금까지 먹은 도토리 수
    private int userAcorns; // 유저가 보유 중인 도토리 수

}
