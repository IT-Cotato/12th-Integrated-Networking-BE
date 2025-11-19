package com.team3.backend.domain.application;

import com.team3.backend.domain.dto.response.WeatherResponse;
import com.team3.backend.domain.infra.AirQualityClient;
import com.team3.backend.domain.infra.WeatherClient;
import com.team3.backend.domain.infra.dto.AirQualityDto;
import com.team3.backend.domain.infra.dto.OpenWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient;
    private final AirQualityClient airQualityClient;

    public WeatherResponse getWeather(double lat, double lon) {

        OpenWeatherDto weather = weatherClient.getCurrentWeather(lat, lon);

        AirQualityDto air = airQualityClient.getAirQuality(lat,lon);

        OpenWeatherDto.Main main = weather.getMain();
        OpenWeatherDto.Wind wind = weather.getWind();
        OpenWeatherDto.Weather condition = weather.getWeather().get(0);
        OpenWeatherDto.Sys sys = weather.getSys();

        double pm10 = air.getList().get(0).getComponents().getPm10();
        double pm25 = air.getList().get(0).getComponents().getPm2_5();

        String windDirection = convertWindDirection(wind.getDeg());
        String time = convertTime(sys.getSunrise(), sys.getSunset());
        String sunrise = convertToHHmm(sys.getSunrise());

        String pm10Level = convertPm10(pm10);
        String pm25Level = convertPm25(pm25);

        return WeatherResponse.builder()
                .temperature(main.getTemp())
                .feelsLike(main.getFeels_like())
                .humidity(main.getHumidity())
                .windSpeed(wind.getSpeed())
                .windDirection(windDirection)
                .condition(condition.getDescription())
                .time(time)
                .sunrise(sunrise)
                .airQuality(
                        WeatherResponse.AirQuality.builder()
                                .pm10(pm10Level)
                                .pm2_5(pm25Level)
                                .build()
                )
                .build();
    }

    private String convertWindDirection(int deg) {
        if (deg >= 315 || deg < 45) return "북풍";
        if (deg >= 45 && deg < 135) return "동풍";
        if (deg >= 135 && deg < 225) return "남풍";
        return "서풍";
    }

    private String convertPm10(double pm10) {
        if (pm10 <= 30) return "좋음";
        if (pm10 <= 80) return "보통";
        if (pm10 <= 150) return "나쁨";
        return "매우나쁨";
    }

    private String convertPm25(double pm25) {
        if (pm25 <= 15) return "좋음";
        if (pm25 <= 35) return "보통";
        if (pm25 <= 75) return "나쁨";
        return "매우나쁨";
    }

    private String convertTime(long sunrise, long sunset) {
        long now = System.currentTimeMillis() / 1000;

        if (now >= sunrise && now < sunset) {
            return "주간";
        }
        return "야간";
    }

    private String convertToHHmm(long unix) {
        java.time.LocalTime time =
                java.time.Instant.ofEpochSecond(unix)
                        .atZone(java.time.ZoneId.of("Asia/Seoul"))
                        .toLocalTime();
        return time.toString().substring(0, 5);
    }
}
