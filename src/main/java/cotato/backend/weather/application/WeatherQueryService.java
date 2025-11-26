package cotato.backend.weather.application;

import org.springframework.stereotype.Service;

import cotato.backend.weather.infra.client.WeatherApiClient;
import cotato.backend.weather.infra.dto.ExternalAirPollutionResponse;
import cotato.backend.weather.infra.dto.ExternalOneCallResponse;
import cotato.backend.weather.infra.dto.WeatherViewResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherQueryService {

	private final WeatherApiClient weatherApiClient;

	public WeatherViewResponse getWeather(double lat, double lon) {
		ExternalOneCallResponse oneCall = weatherApiClient.getOneCallWeather(
			lat,
			lon,
			"metric",
			"kr",
			"minutely,alerts"
		);
		ExternalAirPollutionResponse air = weatherApiClient.getAirPollution(
			lat,
			lon
		);

		return WeatherViewMapper.toView(oneCall, air.getList().get(0).getComponents().getPm10(),
			air.getList().get(0).getComponents().getPm25());
	}
}
