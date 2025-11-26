package cotato.backend.weather.infra.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalOneCallResponse {

	private double lat;
	private double lon;
	private String timezone;

	@JsonProperty("timezone_offset")
	private int timezoneOffset;

	private Current current;
	private List<Hourly> hourly;
	private List<Daily> daily;

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Current {
		private long dt;
		private long sunrise;
		private long sunset;

		@JsonProperty("temp")
		private double temp;

		@JsonProperty("feels_like")
		private double feelsLike;

		private int humidity;

		@JsonProperty("wind_speed")
		private double windSpeed;

		@JsonProperty("wind_deg")
		private double windDeg;

		private double uvi;

		private List<Weather> weather;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Hourly {

		private long dt;

		@JsonProperty("temp")
		private double temp;

		@JsonProperty("feels_like")
		private double feelsLike;

		private int humidity;

		@JsonProperty("wind_speed")
		private double windSpeed;

		@JsonProperty("wind_deg")
		private double windDeg;

		private double pop; // 강수확률 (0~1)

		private List<Weather> weather;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Daily {

		private long dt;
		private Temp temp;
		private double pop;
		private List<Weather> weather;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Temp {
		private double min; // 하루 전체 중 최저 온도
		private double max; // 하루 전체 중 최고 온도
		private double day; // 낮 온도 (오후)
		private double morn; // 아침 온도 (오전)
		private double eve; // 저녁 온도
		private double night; // 밤 온도
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Weather {
		private int id;
		private String main;
		private String description;
		private String icon;
	}
}
