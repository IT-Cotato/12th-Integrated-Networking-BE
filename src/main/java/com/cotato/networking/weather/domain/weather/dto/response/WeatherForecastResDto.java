package com.cotato.networking.weather.domain.weather.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastResDto {
    private List<WeatherItem> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherItem {
        private Main main;
        private List<Weather> weather;
        private double pop; // 0.0 ~ 1.0
        private String dt_txt;

        public LocalDate getKstDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime utcDateTime = LocalDateTime.parse(this.dt_txt, formatter);
            ZonedDateTime seoulDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Seoul"));
            return seoulDateTime.toLocalDate();
        }

        public int getHour() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime utcDateTime = LocalDateTime.parse(this.dt_txt, formatter);
            ZonedDateTime seoulTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Seoul"));
            return seoulTime.getHour();
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Main {
        private double temp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather {
        private String main;
        private String description;
    }

}

