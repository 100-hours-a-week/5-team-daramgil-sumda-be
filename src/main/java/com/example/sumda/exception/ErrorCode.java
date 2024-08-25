package com.example.sumda.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ai
    AI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI 서버 오류"),

    // official
    OFFICIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지를 찾을 수 없습니다."),

    // inquiry
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND,"입력 값이 잘못 되었습니다"),

    // airInfo
    AIR_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 위치의 대기 정보를 찾을 수 없습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 url 요청입니다."),

    // 위치 정보 오류
    LOCATION_ERROR(HttpStatus.BAD_REQUEST, "위치 정보가 잘못되었습니다"),

    // 서버 오류
    SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,  "서버 오류"),
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "json 파싱 오류"),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "null pointer 오류");

    private final HttpStatus httpStatus;

    private final String message;

}
