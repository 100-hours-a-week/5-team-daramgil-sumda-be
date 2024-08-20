package com.example.sumda.service;

import com.example.sumda.entity.User;
import com.example.sumda.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long createUser(String email, String nickname, String profileImageUrl) {
        // 기본 생성자와 setter 메서드를 사용하여 User 객체 생성
        User user = new User();
        user.setKakaoEmail(email);           // 이메일을 Kakao Email로 설정
        user.setNickname(nickname);          // 닉네임 설정
        user.setProfileImageUrl(profileImageUrl); // 프로필 이미지 URL 설정

        userRepository.save(user);
        log.info("새로운 회원 저장 완료");
        return user.getId();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}