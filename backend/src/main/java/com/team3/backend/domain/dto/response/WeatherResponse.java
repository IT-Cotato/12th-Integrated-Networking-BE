package com.team3.backend.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "날씨 및 대기질 정보 응답 DTO")
public class WeatherResponse {

    @Schema(description = "현재 온도 (°C)", example = "25.5")
    private double temperature;

    @Schema(description = "체감 온도 (°C)", example = "26.1")
    private double feelsLike;
    @Schema(description = "습도 (%)", example = "60")
    private int humidity;
    @Schema(description = "풍속 (m/s)", example = "3.2")
    private double windSpeed;
    @Schema(description = "풍향", example = "북동")
    private String windDirection;
    @Schema(description = "현재 날씨 상태", example = "맑음")
    private String condition;
    @Schema(description = "데이터 기준 시간", example = "2025-11-23 15:00")
    private String time;
    @Schema(description = "일출 시간", example = "07:00")
    private String sunrise;
    @Schema(description = "대기질 정보")
    private AirQuality airQuality;

    @Getter
    @Builder
    @Schema(description = "대기질 상세 정보")
    public static class AirQuality {
        @Schema(description = "미세먼지 농도 (PM10)", example = "25")
        private String pm10;
        @Schema(description = "초미세먼지 농도 (PM2.5)", example = "15")
        private String pm2_5;
    }
}
