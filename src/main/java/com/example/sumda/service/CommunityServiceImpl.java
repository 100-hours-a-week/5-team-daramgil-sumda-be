package com.example.sumda.service;

import com.example.sumda.DTO.CommunityDTO;
import com.example.sumda.entity.Community;
import com.example.sumda.repository.CommunityRepository;
import com.example.sumda.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService{

    private final CommunityRepository communityRepository;

    @Override
    public List<CommunityDTO> getAllPosts() {
        List<Community> communities = communityRepository.findAll();
        return communities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CommunityDTO convertToDTO(Community community) {
        return new CommunityDTO(
                community.getCommunityId(),
                community.getUserId(),
                community.getAddress(),
                community.getLikes(),
                community.getImageURl()
        );
    }
}
