package com.team3.backend.domain.infra.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenWeatherDto {

    private Main main;
    private Wind wind;
    private List<Weather> weather;
    private Sys sys;

    @Data
    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;
    }

    @Data
    public static class Wind {
        private double speed;
        private int deg;
    }

    @Data
    public static class Weather {
        private String main;
        private String description;
    }

    @Data
    public static class Sys {
        private long sunrise;
        private long sunset;
    }
}

