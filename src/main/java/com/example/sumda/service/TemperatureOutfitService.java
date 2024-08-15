package com.example.sumda.service;

import com.example.sumda.entity.TemperatureOutfit;
import org.springframework.stereotype.Service;

@Service
public class TemperatureOutfitService {

    public TemperatureOutfit getRecommendation(double temperature) {
        TemperatureOutfit temperatureOutfit = new TemperatureOutfit();

        if (temperature >= 28) {
            temperatureOutfit.setMessage("28도 이상의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("민소매, 반팔, 반바지");
            temperatureOutfit.setIcon("summer_icon.png");  // Example of an icon
        } else if (temperature >= 23) {
            temperatureOutfit.setMessage("23도~27도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("얇은 셔츠, 면바지");
            temperatureOutfit.setIcon("spring_icon.png");
        } else if (temperature >= 20) {
            temperatureOutfit.setMessage("20도~22도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("얇은 가디건, 긴팔, 면바지, 청바지");
            temperatureOutfit.setIcon("light_jacket_icon.png");
        } else if (temperature >= 17) {
            temperatureOutfit.setMessage("17도~19도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("얇은 니트, 맨투맨, 가디건, 청바지");
            temperatureOutfit.setIcon("sweater_icon.png");
        } else if (temperature >= 12) {
            temperatureOutfit.setMessage("12도~16도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("자켓, 가디건, 야상, 스타킹, 청바지, 면바지");
            temperatureOutfit.setIcon("jacket_icon.png");
        } else if (temperature >= 9) {
            temperatureOutfit.setMessage("9도~11도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹");
            temperatureOutfit.setIcon("coat_icon.png");
        } else if (temperature >= 5) {
            temperatureOutfit.setMessage("5도~8도의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("울 코트, 가죽자켓, 히트텍, 니트, 레깅스");
            temperatureOutfit.setIcon("winter_icon.png");
        } else {
            temperatureOutfit.setMessage("4도 이하의 옷차림 추천 정보를 불러왔습니다.");
            temperatureOutfit.setOutfit("패딩, 두꺼운 코트, 목도리, 기모 제품, 목도리");
            temperatureOutfit.setIcon("heavy_coat_icon.png");
        }

        temperatureOutfit.setTemperature(temperature);
        return temperatureOutfit;
    }
}