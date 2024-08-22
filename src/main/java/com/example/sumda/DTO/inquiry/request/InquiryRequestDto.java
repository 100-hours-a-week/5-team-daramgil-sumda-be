package com.example.sumda.dto.inquiry.request;

import com.example.sumda.entity.Inquiry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class InquiryRequestDto {

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @Email(message = "올바른 이메일을 입력해주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getEmail() {
        return email;
    }

    // DTO에서 엔티티로 변환하는 메서드
    public Inquiry toEntity() {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle(this.title);
        inquiry.setContent(this.content);
        inquiry.setEmail(this.email);
        inquiry.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정
        inquiry.setChecked(false); // 초기 상태 설정
        return inquiry;
    }

}
