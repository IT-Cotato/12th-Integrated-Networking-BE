package com.team3.backend.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherResponse {

    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String windDirection;
    private String condition;
    private String time;
    private String sunrise;

    private AirQuality airQuality;

    @Getter
    @Builder
    public static class AirQuality {
        private String pm10;
        private String pm2_5;
    }
}
