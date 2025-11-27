package cotato.backend.weather.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {

	// 외부 날씨 API 기본 URL
	private String baseUrl;

	// 외부 날씨 API 키
	private String apiKey;
	private String units;
	private String lang;
}


