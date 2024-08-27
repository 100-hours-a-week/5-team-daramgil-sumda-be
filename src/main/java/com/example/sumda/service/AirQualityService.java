package com.example.sumda.service;

import com.example.sumda.dto.airinfo.response.AirPollutionImageResponseDto;
import com.example.sumda.dto.airinfo.response.AirQualityDto;
import com.example.sumda.entity.AirPollutionImages;
import com.example.sumda.entity.AirQualityData;
import com.example.sumda.entity.Locations;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.AirPollutionImageRepository;
import com.example.sumda.repository.AirQualityDataRepository;
import com.example.sumda.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final AirQualityDataRepository airQualityDataRepository;
    private final LocationRepository locationRepository;
    private final AirPollutionImageRepository airPollutionImageRepository;


    // 측정소별 실시간 측정정보 - 대기질
    public AirQualityDto getNowAirQualityData(long id) {
        // 주소 id로 관측소 id 가져오기
        Locations location = locationRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.LOCATION_ERROR));
        Long stationId = location.getStation().getId();
        System.out.println(stationId);

        // 해당 관측소 대기질 정보 조회
        Optional<AirQualityData> airQuality = airQualityDataRepository.findById(stationId);

        // 대기질 정보가 존재하지 않을 경우
        if (airQuality.isEmpty()) {
            throw new CustomException(ErrorCode.AIR_INFO_NOT_FOUND);
        }

        AirQualityDto airQualityDto = new AirQualityDto();
        airQualityDto.setId(airQuality.get().getId());
        airQualityDto.setStation_name(airQuality.get().getStationName());
        airQualityDto.setSo2(airQuality.get().getSo2());
        airQualityDto.setCo(airQuality.get().getCo());
        airQualityDto.setO3(airQuality.get().getO3());
        airQualityDto.setNo2(airQuality.get().getNo2());
        airQualityDto.setPm10(airQuality.get().getPm10());
        airQualityDto.setPm25(airQuality.get().getPm25());
        airQualityDto.setSo2Grade(airQuality.get().getSo2Grade());
        airQualityDto.setCoGrade(airQuality.get().getCoGrade());
        airQualityDto.setO3Grade(airQuality.get().getO3Grade());
        airQualityDto.setNo2Grade(airQuality.get().getNo2Grade());
        airQualityDto.setPm10Grade(airQuality.get().getPm10Grade());
        airQualityDto.setPm25Grade(airQuality.get().getPm25Grade());
        airQualityDto.setKhaiValue(airQuality.get().getKhaiValue());
        airQualityDto.setKhaiGrade(airQuality.get().getKhaiGrade());
        airQualityDto.setDataTime(airQuality.get().getDataTime());

        return airQualityDto;
    }

    //TODO: DB에 시간대별 대기질 정보가 없음
    // 시간별 대기질 정보 조회


    // 대기질 예측 이미지 조회
    public AirPollutionImageResponseDto getAirPollutionImage() {

        List<AirPollutionImages> pm10Images = airPollutionImageRepository.findByInformCode("PM10");
        List<AirPollutionImages> pm25Images = airPollutionImageRepository.findByInformCode("PM25");
        List<AirPollutionImages> o3Images = airPollutionImageRepository.findByInformCode("O3");

        AirPollutionImageResponseDto airPollutionImagesDto = new AirPollutionImageResponseDto();

        // 각 informCode에 해당하는 이미지를 AirPollutionImage로 변환하여 리스트에 추가
        List<AirPollutionImageResponseDto.AirPollutionImage> airPollutionImagesList = new ArrayList<>();

        // PM10 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage pm10ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm10ImageDto.setInformCode("PM10");
        pm10ImageDto.setImages(pm10Images.stream()
                .map(AirPollutionImages::getImageUrl) // AirPollutionImages 객체에서 이미지 URL 추출
                .collect(Collectors.toList()));
        airPollutionImagesList.add(pm10ImageDto);

        // PM25 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage pm25ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        pm25ImageDto.setInformCode("PM25");
        pm25ImageDto.setImages(pm25Images.stream()
                .map(AirPollutionImages::getImageUrl)
                .collect(Collectors.toList()));
        airPollutionImagesList.add(pm25ImageDto);

        // O3 이미지 설정
        AirPollutionImageResponseDto.AirPollutionImage o3ImageDto = new AirPollutionImageResponseDto.AirPollutionImage();
        o3ImageDto.setInformCode("O3");
        o3ImageDto.setImages(o3Images.stream()
                .map(AirPollutionImages::getImageUrl)
                .collect(Collectors.toList()));
        airPollutionImagesList.add(o3ImageDto);

        // 최종 DTO에 설정
        airPollutionImagesDto.setAirPollutionImages(airPollutionImagesList);

        return airPollutionImagesDto;
    }


}
