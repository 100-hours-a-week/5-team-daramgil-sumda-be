package com.example.sumda.service;

import com.example.sumda.DTO.CommunityCreateDTO;
import com.example.sumda.DTO.CommunityDTO;

import java.util.List;

public interface CommunityService {
    List<CommunityDTO> getAllPosts();
    CommunityDTO createPost(CommunityCreateDTO communityCreateDTO);
    void deletePost(long communityId);
    void likePost(long communityId);
}
