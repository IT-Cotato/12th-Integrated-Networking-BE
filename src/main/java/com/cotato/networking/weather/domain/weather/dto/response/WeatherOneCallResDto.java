package com.cotato.networking.weather.domain.weather.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherOneCallResDto {

    private Current current;
    private List<Hourly> hourly;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Current {
        private double temp;
        private double feels_like;
        private int humidity;
        private double wind_speed;
        private int wind_deg;
        private double uvi;
        private List<Weather> weather;
        private long sunrise;
        private long sunset;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hourly {
        private long dt;
        private double temp;
        private double feels_like;
        private int humidity;
        private double wind_speed;
        private int wind_deg;
        private double uvi;
        private long sunrise;
        private List<Weather> weather;
    }
}
