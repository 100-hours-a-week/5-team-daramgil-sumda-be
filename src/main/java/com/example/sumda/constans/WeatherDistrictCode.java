package com.example.sumda.constans;

public enum WeatherDistrictCode {
    // 서울, 인천, 경기도
    SEOUL_INCHEON_GYEONGGI("서울특별시", "11B00000"),
    INCHEON("인천광역시", "11B00000"),
    GYEONGGI("경기도", "11B00000"),

    // 강원도 영서 지역
    CHUNCHEON("춘천시", "11D10000"),
    WONJU("원주시", "11D10000"),
    HONGCHEON("홍천군", "11D10000"),
    HWAEONG("횡성군", "11D10000"),
    YEONGWOL("영월군", "11D10000"),
    JEONGSEON("정선군", "11D10000"),
    CHEORWON("철원군", "11D10000"),
    HWACHEON("화천군", "11D10000"),
    YANGGU("양구군", "11D10000"),
    INJE("인제군", "11D10000"),
    PYEONGCHANG("평창군", "11D10000"), // 대관령면 제외

    // 강원도 영동 지역
    GANGNEUNG("강릉시", "11D20000"),
    DONGHAE("동해시", "11D20000"),
    SAMCHEOK("삼척시", "11D20000"),
    SOKCHO("속초시", "11D20000"),
    TAEBAEK("태백시", "11D20000"),
    GOSEONG("고성군", "11D20000"),
    YANGYANG("양양군", "11D20000"),
    PYEONGCHANG_DAEGWALLYEONG("평창군 대관령면", "11D20000"), // 특이 케이스

    // 대전, 세종, 충청남도
    DAEJEON("대전광역시", "11C20000"),
    SEJONG("세종특별자치시", "11C20000"),
    CHUNGCHEONGNAM("충청남도", "11C20000"),

    // 충청북도
    CHUNGCHEONGBUK("충청북도", "11C10000"),

    // 광주, 전라남도
    GWANGJU("광주광역시", "11F20000"),
    JEOLLANAM("전라남도", "11F20000"),

    // 전라북도
    JEOLLABUK("전북특별자치도", "11F10000"),

    // 대구, 경상북도
    DAEGU("대구광역시", "11H10000"),
    GYEONGBUK("경상북도", "11H10000"),

    // 부산, 울산, 경상남도
    BUSAN("부산광역시", "11H20000"),
    ULSAN("울산광역시", "11H20000"),
    GYEONGNAM("경상남도", "11H20000"),

    // 제주도
    JEJU("제주특별자치도", "11G00000");

    private final String district;
    private final String code;

    WeatherDistrictCode(String district, String code) {
        this.district = district;
        this.code = code;
    }

    public String getDistrict() {
        return district;
    }

    public String getCode() {
        return code;
    }

    public static String getCodeByDistrict(String district) {
        for (WeatherDistrictCode value : WeatherDistrictCode.values()) {
            if (value.getDistrict().equals(district)) {
                return value.getCode();
            }
        }
        return "Unknown"; // default value if district not found
    }
}