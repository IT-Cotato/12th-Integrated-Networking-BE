package cotato.backend.weather.application;

import feign.FeignException;
import org.springframework.stereotype.Service;

import cotato.backend.weather.infra.client.WeatherApiClient;
import cotato.backend.weather.infra.dto.ExternalAirPollutionResponse;
import cotato.backend.weather.infra.dto.ExternalOneCallResponse;
import cotato.backend.weather.infra.dto.WeatherViewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherQueryService {

	private final WeatherApiClient weatherApiClient;

    // 🚨 getWeather 메서드에 try-catch 블록 추가
    public WeatherViewResponse getWeather(double lat, double lon) {

        // 🚩 1. 디버깅 로그: 외부 호출 시작 기록
        log.info("OpenWeatherMap API 호출 시작: Lat={}, Lon={}", lat, lon);

        try {
            // 2. OpenWeatherMap OneCall API 호출
            ExternalOneCallResponse oneCall = weatherApiClient.getOneCallWeather(
                    lat,
                    lon,
                    "metric",
                    "kr",
                    "minutely,alerts"
            );

            // 3. Air Pollution API 호출
            ExternalAirPollutionResponse air = weatherApiClient.getAirPollution(
                    lat,
                    lon
            );

            log.info("OpenWeatherMap 호출 성공. 날씨 및 미세먼지 데이터 확보 완료.");

            // 4. 데이터 매핑 후 반환 (Mapper는 존재한다고 가정)
            return WeatherViewMapper.toView(oneCall, air.getList().get(0).getComponents().getPm10(),
                    air.getList().get(0).getComponents().getPm25());

        } catch (FeignException e) {
            // 🚨 5. 핵심 디버깅: 외부 API 통신 실패 시 로그 기록 및 예외 처리
            log.error("====================== OpenWeatherMap API 통신 실패 ======================");
            log.error("상태 코드 (Status): {}", e.status()); // 401, 400 등 실제 오류 코드
            log.error("요청 URL: {}", e.request().url());
            log.error("응답 바디 (오류 메시지): {}", e.contentUTF8()); // API 키 오류 상세 메시지
            log.error("========================================================================");

            // 500 에러 대신 Custom Runtime Exception을 던져서 Global Exception Handler가 처리하도록 함
            throw new RuntimeException("외부 날씨 API 호출에 실패했습니다. Status: " + e.status(), e);
        }
    }
}
