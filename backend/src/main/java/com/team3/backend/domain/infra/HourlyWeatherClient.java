package com.team3.backend.domain.infra;

import com.team3.backend.domain.infra.dto.HourlyWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class HourlyWeatherClient {

    private final WebClient webClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    private static final String URL = "https://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&appid=%s";

    public HourlyWeatherDto getHourlyWeather(double lat, double lon) {
        String finalUrl = String.format(URL, lat, lon, apiKey);

        return webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(HourlyWeatherDto.class)
                .block();
    }
}
