package com.example.sumda.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AirInfoDTO {
    private String status;
    private String message;
    private String khaiGrade; // 통합대기환경 지수
    private String khaiValue; // 통합대기환경 수치
    private String pm10Grade; // 미세먼지 지수
    private String pm10Value; // 미세먼지 농도
    private String pm25Grade; // 초미세먼지 지수
    private String pm25Value; // 초미세먼지 농도
    private String o3Grade; // 오존 지수
    private String o3Value; // 오존 농도
    private String no2Grade; // 이산화질소 지수
    private String no2Value; // 이산화질소 농도
    private String coGrade; // 일산화탄소 지수
    private String coValue; // 일산화탄소 농도
    private String so2Grade; // 아황산가스 지수
    private String so2Value; // 아황산가스 농도

    private String pm25Grade1h; // 초미세먼지
    private String pm10Value24; // 미세먼지 24시간 예측이동농도
    private String pm10Grade1h; // 미세먼지 1시간 등급
    private String pm25Flag; // 초미세먼지 플래그
    private String no2Flag; //이산화질소 플래그
    private String mangName; // 즉정망 정보
    private String stationName; // 측성소명
    private String stationCode; // 측정소 코드
    private String coFlag; // 일산화탄소 지수
    private String pm10Flag; // 미세먼지 플래그
    private String pm25Value24; //초미세먼지 24시간 예측이동농도
    private String o3Flag; // 오존 플래그
    private String so2Flag; // 아황산가스 플래그
    private String dataTime; // 측정일

}
