package cotato.backend.weather.infra.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalAirPollutionResponse {

	private List<AirData> list;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AirData {

		private Components components;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Components {

		// 초미세먼지
		private Double pm25;

		// 미세먼지
		private Double pm10;
	}
}