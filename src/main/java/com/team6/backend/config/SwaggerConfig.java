package com.team6.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI CotatoAPI() {
		Info info = new Info()
			.title("Cotato 12th Integrated Team6 API")
			.description("코테이토 12기 통합 네트워킹 과제 6조 API 명세서")
			.version("1.0.0");


		return new OpenAPI()
			.addServersItem(new Server().url("/"))
			.info(info);
	}
}