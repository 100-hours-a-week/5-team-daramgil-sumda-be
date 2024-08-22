package com.example.sumda.controller;

import com.example.sumda.service.OfficialService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/official")
public class OfficialController {

    private final OfficialService officialService;

    @GetMapping
    public ResponseEntity<?> getAllOfficials() {
        return ResponseUtils.createResponse(HttpStatus.OK, "공지사항 조회 완료", officialService.getAllOfficials());
    }
}
