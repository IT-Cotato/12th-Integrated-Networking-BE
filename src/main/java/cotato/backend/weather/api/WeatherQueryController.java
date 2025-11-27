package cotato.backend.weather.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cotato.backend.weather.application.WeatherQueryService;
import cotato.backend.weather.infra.dto.WeatherViewResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WeatherQueryController {
	private final WeatherQueryService weatherQueryService;

	@GetMapping("/api/weather")
	public WeatherViewResponse getWeather(
		@RequestParam("lat") double latitude,
		@RequestParam("lon") double longitude
	) {
		return weatherQueryService.getWeather(latitude, longitude);
	}
}

