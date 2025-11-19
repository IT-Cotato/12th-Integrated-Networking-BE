package com.team3.backend.domain.infra.dto;

import lombok.Data;

import java.util.List;

@Data
public class AirQualityDto {

    private List<Item> list;

    @Data
    public static class Item {
        private Main main;
        private Components components;
    }

    @Data
    public static class Main {
        private int aqi;  // 대기질 지수 (1~5)
    }

    @Data
    public static class Components {
        private double pm2_5;
        private double pm10;
    }
}