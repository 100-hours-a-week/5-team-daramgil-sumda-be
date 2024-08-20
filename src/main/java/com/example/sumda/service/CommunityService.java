package com.example.sumda.service;

import com.example.sumda.DTO.CommunityCreateDTO;
import com.example.sumda.DTO.CommunityDTO;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

public interface CommunityService {
    List<CommunityDTO> getAllPosts();

    CommunityDTO createPost(CommunityCreateDTO communityCreateDTO);

    void deletePost(long communityId);

    void likePost(long communityId);

    // 이미지 저장을 위한 메서드
    String saveImage(MultipartFile imageFile) throws IOException;
}
