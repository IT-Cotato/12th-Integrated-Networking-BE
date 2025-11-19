package com.team3.backend.domain.infra;

import com.team3.backend.domain.infra.dto.AirQualityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AirQualityClient {

    private final WebClient webClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    public AirQualityDto getAirQuality(double lat, double lon) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s",
                lat, lon, apiKey
        );

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(AirQualityDto.class)
                .block();
    }
}
