package com.example.sumda.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenResponseDto {

    @JsonProperty("access_token")
    private String accessToken;


}
