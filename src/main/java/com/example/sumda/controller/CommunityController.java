package com.example.sumda.controller;

import com.example.sumda.DTO.CommunityDTO;
import com.example.sumda.service.CommunityService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    // 게시글 목록 불러오기
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<CommunityDTO> posts = communityService.getAllPosts();
            return ResponseUtils.createResponse(HttpStatus.OK, "게시글 목록을 성공적으로 불러왔습니다.", posts);
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 목록을 불러오는데 실패했습니다.");
        }
    }

}
