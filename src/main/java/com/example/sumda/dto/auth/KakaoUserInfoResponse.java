package com.example.sumda.dto.auth;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoUserInfoResponse{
    private Map<String, Object> attributes; // getAttributes()
    public KakaoUserInfoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }


    public String getNickname() {
        Object kakaoAccountObject = attributes.get("kakao_account");
        LinkedHashMap kakaoAccountMap = (LinkedHashMap) kakaoAccountObject;
        Object profileObject = kakaoAccountMap.get("profile");
        LinkedHashMap profileMap = (LinkedHashMap) profileObject;
        return (String) profileMap.get("nickname");
    }

//    public String getName() {
//        return (String) attributes.get("name");
//    }
}