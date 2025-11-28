package com.example.weather.domain.health;

import com.example.weather.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

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

    @Operation(
            summary = "데이터베이스 연결 확인",
            description = "MySQL 데이터베이스 연결 상태를 확인합니다."
    )
    @GetMapping("/db")
    public ApiResponse<DatabaseHealthResponse> databaseHealthCheck() {
        try {
            // MySQL 버전 확인 쿼리 실행
            String version = (String) entityManager
                    .createNativeQuery("SELECT VERSION()")
                    .getSingleResult();

            // 현재 데이터베이스 이름 확인
            String database = (String) entityManager
                    .createNativeQuery("SELECT DATABASE()")
                    .getSingleResult();

            DatabaseHealthResponse response = new DatabaseHealthResponse(
                    "OK",
                    "Database connection successful",
                    database,
                    version,
                    LocalDateTime.now()
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            DatabaseHealthResponse response = new DatabaseHealthResponse(
                    "ERROR",
                    "Database connection failed: " + e.getMessage(),
                    null,
                    null,
                    LocalDateTime.now()
            );
            return ApiResponse.error("DB_CONNECTION_ERROR", "데이터베이스 연결 실패", response);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class HealthCheckResponse {
        private String status;
        private String message;
        private LocalDateTime timestamp;
    }

    @Getter
    @AllArgsConstructor
    public static class DatabaseHealthResponse {
        private String status;
        private String message;
        private String database;
        private String version;
        private LocalDateTime timestamp;
    }
}
