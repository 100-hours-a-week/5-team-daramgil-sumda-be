package com.example.sumda.dto.ai.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClothesReasonResponseDto {
    private String clothesName;
    private String reason;
}
