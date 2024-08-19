package com.example.sumda.service;

import com.example.sumda.entity.TemperatureOutfit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemperatureOutfitService {

    public List<TemperatureOutfit> getAllRecommendations() {
        List<TemperatureOutfit> recommendations = new ArrayList<>();

        // 28도 이상
        TemperatureOutfit outfitAbove28 = new TemperatureOutfit();
        outfitAbove28.setMessage("28도 이상의 옷차림 추천 정보를 불러왔습니다.");
        outfitAbove28.setOutfit("민소매, 반팔, 반바지");
        outfitAbove28.setIcon("summer_icon.png");
        recommendations.add(outfitAbove28);

        // 23도 ~ 27도
        TemperatureOutfit outfit23to27 = new TemperatureOutfit();
        outfit23to27.setMessage("23도~27도의 옷차림 추천 정보를 불러왔습니다.");
        outfit23to27.setOutfit("얇은 셔츠, 면바지");
        outfit23to27.setIcon("spring_icon.png");
        recommendations.add(outfit23to27);

        // 20도 ~ 22도
        TemperatureOutfit outfit20to22 = new TemperatureOutfit();
        outfit20to22.setMessage("20도~22도의 옷차림 추천 정보를 불러왔습니다.");
        outfit20to22.setOutfit("얇은 가디건, 긴팔, 면바지, 청바지");
        outfit20to22.setIcon("light_jacket_icon.png");
        recommendations.add(outfit20to22);

        // 17도 ~ 19도
        TemperatureOutfit outfit17to19 = new TemperatureOutfit();
        outfit17to19.setMessage("17도~19도의 옷차림 추천 정보를 불러왔습니다.");
        outfit17to19.setOutfit("얇은 니트, 맨투맨, 가디건, 청바지");
        outfit17to19.setIcon("sweater_icon.png");
        recommendations.add(outfit17to19);

        // 12도 ~ 16도
        TemperatureOutfit outfit12to16 = new TemperatureOutfit();
        outfit12to16.setMessage("12도~16도의 옷차림 추천 정보를 불러왔습니다.");
        outfit12to16.setOutfit("자켓, 가디건, 야상, 스타킹, 청바지, 면바지");
        outfit12to16.setIcon("jacket_icon.png");
        recommendations.add(outfit12to16);

        // 9도 ~ 11도
        TemperatureOutfit outfit9to11 = new TemperatureOutfit();
        outfit9to11.setMessage("9도~11도의 옷차림 추천 정보를 불러왔습니다.");
        outfit9to11.setOutfit("자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹");
        outfit9to11.setIcon("coat_icon.png");
        recommendations.add(outfit9to11);

        // 5도 ~ 8도
        TemperatureOutfit outfit5to8 = new TemperatureOutfit();
        outfit5to8.setMessage("5도~8도의 옷차림 추천 정보를 불러왔습니다.");
        outfit5to8.setOutfit("울 코트, 가죽자켓, 히트텍, 니트, 레깅스");
        outfit5to8.setIcon("winter_icon.png");
        recommendations.add(outfit5to8);

        // 4도 이하
        TemperatureOutfit outfitBelow4 = new TemperatureOutfit();
        outfitBelow4.setMessage("4도 이하의 옷차림 추천 정보를 불러왔습니다.");
        outfitBelow4.setOutfit("패딩, 두꺼운 코트, 목도리, 기모 제품, 목도리");
        outfitBelow4.setIcon("heavy_coat_icon.png");
        recommendations.add(outfitBelow4);

        return recommendations;
    }
}