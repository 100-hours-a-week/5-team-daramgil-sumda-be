package com.example.sumda.service;

import com.example.sumda.DTO.CommunityCreateDTO;
import com.example.sumda.DTO.CommunityDTO;
import com.example.sumda.entity.Community;
import com.example.sumda.repository.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    private final String IMAGE_UPLOAD_DIR = "src/main/resources/community/image/";

    // 모든 게시글 불러오기
    @Override
    public List<CommunityDTO> getAllPosts() {
        List<Community> communities = communityRepository.findAll();
        return communities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    @Override
    public String saveImage(MultipartFile imageFile) throws IOException {
        // 이미지 파일 확장자 검증 (png, jpg, jpeg)
        String fileName = imageFile.getOriginalFilename();
        if (fileName == null || !isSupportedImageType(fileName)) {
            throw new IllegalArgumentException("지원되지 않는 이미지 형식입니다. (png, jpg, jpeg만 허용)");
        }

        // 저장할 파일 이름을 설정 (UUID를 사용하여 고유한 파일 이름 생성)
        String newFileName = UUID.randomUUID() + "." + getExtension(fileName);

        // 파일 저장 경로
        Path imagePath = Paths.get("src/main/resources/static/community/image/" + newFileName);
        Files.createDirectories(imagePath.getParent()); // 디렉토리가 없는 경우 생성
        Files.write(imagePath, imageFile.getBytes());

        // 파일 경로를 리턴 (DB에 저장할 값)
        return "/community/image/" + newFileName;
    }

    private boolean isSupportedImageType(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg");
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
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
