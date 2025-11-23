package com.team3.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("12th Integrated Networking BE API")
                        .version("1.0.0")
                        .description("코테이토 12기 통합 네트워킹 백엔드 프로젝트 API 명세서"));
    }
}
