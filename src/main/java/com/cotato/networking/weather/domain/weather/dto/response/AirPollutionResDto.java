package com.cotato.networking.weather.domain.weather.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirPollutionResDto {

    @Schema(description = "위치 좌표 정보")
    private Coord coord;
    private List<AirData> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coord {
        private double lon;
        private double lat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirData {
        @Schema(description = "대기질 지수 정보")
        private Main main;

        @Schema(description = "대기 오염 성분 농도 정보")
        private Components components;

        @Schema(description = "데이터 측정 시간 (Unix timestamp)", example = "1747879386")
        private long dt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Main {
        @Schema(description = "대기질 지수 (AQI) 값, 1: 좋음(Good), 2: 보통(Fair), 3: 보통(Moderate), 4: 나쁨(Poor), 5: 매우 나쁨(Very Poor)", example = "1")
        private int aqi;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "대기 오염 성분 농도 (μg/m³)")
    public static class Components {

        @Schema(description = "일산화탄소 농도", example = "99.94")
        private double co;

        @Schema(description = "일산화질소 농도", example = "0.03")
        private double no;

        @Schema(description = "이산화질소 농도", example = "0.11")
        private double no2;

        @Schema(description = "오존 농도", example = "38.61")
        private double o3;

        @Schema(description = "아황산가스 농도", example = "0.16")
        private double so2;

        @Schema(description = "미세먼지 PM2.5 농도", example = "1.72")
        private double pm2_5;

        @Schema(description = "미세먼지 PM10 농도", example = "7.33")
        private double pm10;

        @Schema(description = "암모니아 농도", example = "0")
        private double nh3;
    }
}

