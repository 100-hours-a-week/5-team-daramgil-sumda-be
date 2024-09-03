package com.example.sumda.dto.squirrel.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class SquirrelCollectionResponseDto {

    private String sqrType; // 다람쥐 종류
    private Timestamp startDate; // 분양 날짜
    private Timestamp endDate; // 독립 날짜


}
