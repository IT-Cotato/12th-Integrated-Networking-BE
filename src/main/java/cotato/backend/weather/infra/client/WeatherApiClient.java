package cotato.backend.weather.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cotato.backend.weather.infra.config.WeatherFeignConfig;
import cotato.backend.weather.infra.dto.ExternalAirPollutionResponse;
import cotato.backend.weather.infra.dto.ExternalOneCallResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FeignClient(
	name = "openWeatherApiClient",
	url = "${weather.api.base-url}",
	configuration = WeatherFeignConfig.class
)
public interface WeatherApiClient {

	// GET /data/3.0/onecall?lat={lat}&lon={lon}&exclude={exclude}&appid={apiKey}&units=metric&lang=kr
	@GetMapping("/data/3.0/onecall")
	ExternalOneCallResponse getOneCallWeather(
		@RequestParam("lat") double latitude,
		@RequestParam("lon") double longitude,
		@RequestParam("units") String units,
		@RequestParam("lang") String lang,
		@RequestParam(value = "exclude", required = false) String exclude
	);

	// GET /data/2.5/air_pollution?lat={lat}&lon={lon}&appid={apiKey}
	@GetMapping("/data/2.5/air_pollution")
	ExternalAirPollutionResponse getAirPollution(
		@RequestParam("lat") double latitude,
		@RequestParam("lon") double longitude
	);
}
