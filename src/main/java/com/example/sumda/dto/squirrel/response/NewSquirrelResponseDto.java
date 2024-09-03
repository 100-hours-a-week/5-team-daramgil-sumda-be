package com.example.sumda.dto.squirrel.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewSquirrelResponseDto {

    private String type;
    private int level;
    private int feed;
}
