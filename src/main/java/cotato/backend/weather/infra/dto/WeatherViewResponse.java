package cotato.backend.weather.infra.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class WeatherViewResponse {

	private final CurrentSection current;
	private final List<HourlySection> hourly;
	private final List<DailySection> daily;

	@Getter
	@Builder
	@ToString
	public static class CurrentSection {

		// 12.2도
		private final String temperature;

		//아이콘 코드 / URL
		// (04d -> https://openweathermap.org/img/wn/04d@2x.png)
		private final String iconCode;
		private final String iconUrl;

		// "주간" or "야간"
		private final String timeOfDay;

		// "흐림", "맑음", "튼구름" ...
		private final String skyCondition;

		// "야간/흐림"
		private final String conditionSummary;

		// "체감 9.0도"
		private final String feelsLike;

		// "습도 48%"
		private final String humidity;

		// "남동풍"
		private final String windDirection;

		// "0.4m/s"
		private final String windSpeed;

		// 미세먼지 등급 ("좋음". "보통", ...)
		private final String pm10Grade;

		// 초미세먼지 등급 ("좋음", "보통", ...)
		private final String pm25Grade;

		// 자외선 등급 ("낮음", "보통", "위험" ...)
		private final String uvGrade;

		// 일출 ( "05:44")
		private final String sunriseTime;
	}

	@Getter
	@Builder
	@ToString
	public static class HourlySection {
		// "03시"
		private final String time;

		// 아이콘 코드 / URL
		private final String iconCode;
		private final String iconUrl;

		// "8도"
		private final String temperature;
	}

	@Getter
	@Builder
	@ToString
	public static class DailySection {
		// "수", "목" 같은 요일
		private final String dayOfWeek;
		// "11.27" 같은 날짜
		private final String dateLabel;

		// 오전 정보
		private final String amIconCode;
		private final String amIconUrl;
		private final String amTemperature;        // "8도"
		private final String amPrecipitationProb;  // 강수확률: "8%"

		// 오후 정보
		private final String pmIconCode;
		private final String pmIconUrl;
		private final String pmTemperature;        // "19도"
		private final String pmPrecipitationProb;  // 강수확률: "19%"

	}
}
