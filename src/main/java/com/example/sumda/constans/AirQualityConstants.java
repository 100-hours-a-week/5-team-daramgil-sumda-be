package com.example.sumda.constans;

public class AirQualityConstants {
    // 대기질 평가 요청에 사용되는 상수
    public static final String AIR_RESPONSE_PROMPT =
            "우리 서비스는 귀엽게 대답을 해줘야 돼" +
                    "<다음> 아래의 데이터를 학습하고 거기에 대해서 오늘의 대기질을 딱 한줄로 평가해줘" +
                    "<다음>";

    public static final String REVIEW_RESPONSE_PROMPT =
            "우리 서비스는 귀엽게 대답을 해줘야 돼." +
                    "<다음> 아래의 데이터를 학습하고 거기에 대해서 오늘의 대기질과 날씨의 대한 행동 요령을 추천 해줘." +
                    "<다음>";

    public static final String WEATHER_RESPONSE_PROMPT =
            "우리 서비스는 귀엽게 대답을 해줘야 돼." +
                    "<다음> 아래의 데이터를 학습하고 거기에 대해서 오늘의 날씨를을 딱 한줄로 평가해줘." +
                    "<다음>";

    public static final String ACTIVITY_RESPONSE_PROMPT =
            "<다음> 아래에 데이터를 보고 오늘의 온도를 보고 할만한 활동을 추천해주고 오늘의 대기질과 날씨 데이터와 관련지어서 이유도 설명해줘야돼. activityName과 reason을 나눠서 여러개를 한글로 추천해주고 json 형식으로 출력해줘 \n" +
                    "<다음>\n";

    public static final String  CLOTHES_RESPONSE_PROMPT =
            "<다음> 아래에 데이터를 보고 오늘의 옷을 추천해주는데 clothesName과 reason을 나눠서 여러개를 한글로 추천해주고 json 형식으로 출력해줘 \n" +
                    "<다음>";
}
