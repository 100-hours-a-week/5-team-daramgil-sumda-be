package com.example.sumda.controller;

import com.example.sumda.DTO.CommunityCreateDTO;
import com.example.sumda.DTO.CommunityDTO;
import com.example.sumda.service.CommunityService;
import com.example.sumda.utils.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    // 게시글 목록 불러오기
    @GetMapping("/")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<CommunityDTO> posts = communityService.getAllPosts();
            return ResponseUtils.createResponse(HttpStatus.OK, "게시글 목록을 성공적으로 불러왔습니다.", posts);
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 목록을 불러오는데 실패했습니다.");
        }
    }

    // 게시글 작성(이미지 필수)
    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestParam("userId") long userId,
                                        @RequestParam("address") String address,
                                        @RequestParam(value = "image")MultipartFile imageFile) {
        try {
            // 이미지 파일이 없는 경우 예외 발생
            if (imageFile == null || imageFile.isEmpty()) {
                throw new IllegalArgumentException("이미지는 반드시 등록해야 합니다.");
            }

            CommunityCreateDTO communityCreateDTO = new CommunityCreateDTO();
            communityCreateDTO.setUserId(userId);
            communityCreateDTO.setAddress(address);

            // 이미지 파일 처리
            String imagePath = communityService.saveImage(imageFile);
            communityCreateDTO.setImageUrl(imagePath); // 이미지 경로 설정

            CommunityDTO createdPost = communityService.createPost(communityCreateDTO);
            return ResponseUtils.createResponse(HttpStatus.CREATED, "게시글이 성공적으로 등록되었습니다.", createdPost);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.createResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 등록에 실패했습니다.");
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") long communityId) {
        try {
            communityService.deletePost(communityId);
            return ResponseUtils.createResponse(HttpStatus.OK, "게시글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseUtils.createResponse(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제에 실패했습니다.");
        }
    }

    // 게시글 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable("id") long communityId) {
        try {
            communityService.likePost(communityId);
            return ResponseUtils.createResponse(HttpStatus.OK, "게시글에 좋아요를 추가했습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseUtils.createResponse(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseUtils.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요 추가에 실패했습니다.");
        }
    }

}
