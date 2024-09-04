package com.example.sumda.service;

import com.example.sumda.dto.auth.CustomOAuth2User;
import com.example.sumda.dto.auth.KakaoUserInfoResponse;
import com.example.sumda.dto.auth.UserDto;
import com.example.sumda.entity.User;
import com.example.sumda.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);


        KakaoUserInfoResponse kakaoUser = new KakaoUserInfoResponse(oAuth2User.getAttributes());



        Long id = Long.parseLong(kakaoUser.getProviderId());

        User existData = userRepository.findById(id).orElse(null);

        if (existData == null) {

            User userEntity = User.createUser(id, kakaoUser.getNickname(), "email");

            userRepository.save(userEntity);

        }

        UserDto userDto = new UserDto("USER", kakaoUser.getNickname(), kakaoUser.getNickname());

        return new CustomOAuth2User(userDto);
    }
}