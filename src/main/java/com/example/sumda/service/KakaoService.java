package com.example.sumda.service;

import com.example.sumda.DTO.KakaoUserInfoResponseDto;
import com.example.sumda.DTO.KakaoTokenResponseDto;
import com.example.sumda.entity.User;
import com.example.sumda.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class KakaoService {

    private final String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public KakaoService(@Value("${spring.security.oauth2.client.registration.kakao.client_id}") String clientId, UserRepository userRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
        this.KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        this.KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
        this.restTemplate = new RestTemplate();
    }

    public String getAccessTokenFromKakao(String code) {
        String url = KAUTH_TOKEN_URL_HOST + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("grant_type", "authorization_code");
        bodyParams.put("client_id", clientId);
        bodyParams.put("code", code);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(bodyParams, headers);

        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.postForEntity(url, requestEntity, KakaoTokenResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            KakaoTokenResponseDto kakaoTokenResponseDto = response.getBody();
            return kakaoTokenResponseDto.getAccessToken();
        } else {
            throw new RuntimeException("Failed to get Kakao access token");
        }
    }

    @Transactional
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        String url = KAUTH_USER_URL_HOST + "/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // API 요청을 통해 사용자 정보 가져오기
        ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.postForEntity(url, requestEntity, KakaoUserInfoResponseDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            KakaoUserInfoResponseDto userInfo = response.getBody();

            log.info("[ Kakao Service ] Kakao Email ---> {}", userInfo.getKakaoAccount().getEmail());
            log.info("[ Kakao Service ] NickName ---> {}", userInfo.getKakaoAccount().getProfile().getNickName());
            log.info("[ Kakao Service ] ProfileImageUrl ---> {}", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

            // Step 2: Check if user already exists in the DB
            Optional<User> existingUser = userRepository.findByKakaoEmail(userInfo.getKakaoAccount().getEmail());

            if (existingUser.isPresent()) {
                // Update existing user details if necessary
                User user = existingUser.get();
                user.setNickname(userInfo.getKakaoAccount().getProfile().getNickName());
                user.setProfileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
                userRepository.save(user);
            } else {
                // Step 3: Register new user if not found in the DB
                User newUser = new User();
                newUser.setKakaoEmail(userInfo.getKakaoAccount().getEmail());
                newUser.setNickname(userInfo.getKakaoAccount().getProfile().getNickName());
                newUser.setProfileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
                userRepository.save(newUser);
            }

            // Return user info DTO
            return userInfo;

        } else {
            // Handle the case where the user info request fails
            throw new RuntimeException("Failed to get Kakao user info");
        }
    }

    // 로그아웃
    @Transactional
    public void logoutFromKakao(String accessToken) {
        String url = KAUTH_USER_URL_HOST + "/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("[Kakao Service] User logged out from Kakao successfully.");
        } else {
            throw new RuntimeException("Failed to logout from Kakao");
        }
    }

    // 회원 탈퇴
    @Transactional
    public void unlinkKakaoAccount(String accessToken) {
        String url = KAUTH_USER_URL_HOST + "/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(url, requestEntity, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("[Kakao Service] User account unlinked from Kakao successfully.");
        } else {
            throw new RuntimeException("Failed to unlink Kakao account");
        }
    }
}