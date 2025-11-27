package cotato.backend.weather.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WeatherFeignConfig {
	private final WeatherApiProperties weatherApiProperties;

	@Bean
	public RequestInterceptor weatherRequestInterceptor() {
		return template -> {
			template.query("appid", weatherApiProperties.getApiKey());
		};
	}
}
