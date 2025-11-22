package com.team3.backend.api;

import com.team3.backend.domain.application.WeatherService;
import com.team3.backend.domain.dto.response.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "현재 날씨 및 대기질 정보 조회 기능 제공")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "현재 날씨 및 대기질 조회", description = "특정 locationId에 저장된 장소의 현재 날씨와 대기질 정보를 조회합니다.")
    @GetMapping("/current")
    public WeatherResponse getWeather(
            @Parameter(description = "조회할 장소의 ID", example = "1")
            @RequestParam Long locationId) {

        return weatherService.getWeather(locationId);
    }

}
