package com.example.sumda.controller;

import com.example.sumda.DTO.InquiryRequestDTO;
import com.example.sumda.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryRequestDTO inquiryRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder helperText = new StringBuilder("제목, 내용, 이메일을 입력해주세요.");
            bindingResult.getFieldErrors().forEach(error -> {
                switch (error.getField()) {
                    case "title":
                        helperText.setLength(0);
                        helperText.append("제목을 입력해주세요.");
                        break;
                    case "content":
                        helperText.setLength(0);
                        helperText.append("내용을 입력해주세요.");
                        break;
                    case "email":
                        helperText.setLength(0);
                        helperText.append("올바른 이메일을 입력해주세요.");
                        break;
                }
            });

            return ResponseEntity.badRequest().body(new ErrorResponse(helperText.toString(), "VALIDATION_ERROR"));
        }

        inquiryService.createInquiry(inquiryRequestDTO);
        return ResponseEntity.ok().build();
    }

    static class ErrorResponse {
        private String message;
        private String code;

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }
    }
}
