package com.example.weather.domain.health;

import com.example.weather.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Health Check", description = "서버 상태 확인 API")
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Operation(
            summary = "서버 상태 확인",
            description = "서버가 정상적으로 실행 중인지 확인합니다."
    )
    @GetMapping
    public ApiResponse<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = new HealthCheckResponse(
                "OK",
                "Server is running",
                LocalDateTime.now()
        );
        return ApiResponse.success(response);
    }

    @Getter
    @AllArgsConstructor
    public static class HealthCheckResponse {
        private String status;
        private String message;
        private LocalDateTime timestamp;
    }
}
