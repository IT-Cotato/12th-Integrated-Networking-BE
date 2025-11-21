package com.team3.backend.domain.infra.dto;

import lombok.Data;
import java.util.List;

@Data
public class HourlyWeatherDto {

    private String cod;
    private int message;
    private int cnt;
    private List<WeatherItem> list;

    @Data
    public static class WeatherItem {
        private long dt;
        private Main main;
        private List<Weather> weather;
        private String dt_txt;
    }

    @Data
    public static class Main {
        private double temp;
        private int humidity;
    }

    @Data
    public static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;
    }
}
