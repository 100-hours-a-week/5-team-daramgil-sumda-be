package com.example.sumda.service;

import com.example.sumda.DTO.CommunityCreateDTO;
import com.example.sumda.DTO.CommunityDTO;
import com.example.sumda.entity.Community;
import com.example.sumda.repository.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    // 모든 게시글 불러오기
    @Override
    public List<CommunityDTO> getAllPosts() {
        List<Community> communities = communityRepository.findAll();
        return communities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 게시글 작성
    @Override
    public CommunityDTO createPost(CommunityCreateDTO communityCreateDTO) {
        Community newPost = new Community();
        newPost.setUserId(communityCreateDTO.getUserId());
        newPost.setAddress(communityCreateDTO.getAddress());
        newPost.setImageUrl(communityCreateDTO.getImageUrl());
        newPost.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        newPost.setLikes(0); // 초기 좋아요 값은 0

        Community savedCommunity = communityRepository.save(newPost);
        return convertToDTO(savedCommunity); // CommunityDTO로 변환하여 반환
    }

    // 게시글 삭제
    @Override
    public void deletePost(long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));

        communityRepository.delete(community);
    }

    // 좋아요 기능
    @Override
    public void likePost(long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));

        community.setLikes(community.getLikes() + 1);
        communityRepository.save(community);
    }

    // Community 엔티티를 CommunityDTO로 변환
    public CommunityDTO convertToDTO(Community community) {
        return new CommunityDTO(
                community.getCommunityId(),
                community.getUserId(),
                community.getAddress(),
                community.getLikes(),
                community.getImageUrl(),
                community.getCreatedAt()
        );
    }
}
