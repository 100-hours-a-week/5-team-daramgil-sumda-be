package com.example.sumda.dto.official.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AllOfficialListResponseDto {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public static AllOfficialListResponseDto of(Long id, String title, String content, String createdAt, String updatedAt) {
        return new AllOfficialListResponseDto(id, title, content, createdAt, updatedAt);
    }
}
