package cotato.backend.weather.application;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import cotato.backend.weather.infra.dto.ExternalOneCallResponse;
import cotato.backend.weather.infra.dto.WeatherViewResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherViewMapper {

	private static final int HOURLY_HOURS = 24;
	private static final int DAILY_DAYS = 5;

	private static final DateTimeFormatter HOUR_LABEL_FORMAT =
		DateTimeFormatter.ofPattern("HH시");

	private static final DateTimeFormatter TIME_HH_MM_FORMAT =
		DateTimeFormatter.ofPattern("HH:mm");

	private static final DateTimeFormatter DATE_LABEL_FORMAT =
		DateTimeFormatter.ofPattern("M.d");

	public static WeatherViewResponse toView(
		ExternalOneCallResponse source,
		Double pm10,
		Double pm25
	) {
		WeatherViewResponse.CurrentSection current = buildCurrent(source, pm10, pm25);
		log.info("current section: {}", current);
		List<WeatherViewResponse.HourlySection> hourly = buildHourly(source);
		log.info("hourly sections count: {}", hourly.size());
		List<WeatherViewResponse.DailySection> daily = buildDaily(source);
		log.info("daily sections count: {}", daily.size());
		return WeatherViewResponse.builder()
			.current(current)
			.hourly(hourly)
			.daily(daily)
			.build();
	}

	// 현재 카드
	private static WeatherViewResponse.CurrentSection buildCurrent(
		ExternalOneCallResponse source,
		Double pm10,
		Double pm25
	) {
		ExternalOneCallResponse.Current current = source.getCurrent();

		// 현재 카드는 소수점 1자리 유지
		String temperature = formatTemperatureDecimal(current.getTemp());   // 12.2°
		String feelsLike = formatTemperatureDecimal(current.getFeelsLike()); //체감 9.0°
		String humidity = current.getHumidity() + "%"; // 습도 48%

		String skyCondition = skyConditionFromWeatherList(current.getWeather()); // "흐림", "맑음", "튼구름" ...
		String iconCode = iconCodeFromWeatherList(current.getWeather()); // 04d
		String iconUrl = toIconUrl(iconCode);  // https://openweathermap.org/img/wn/04d@2x.png

		boolean night = isNight(current.getDt(), current.getSunrise(), current.getSunset());
		String timeOfDay = night ? "야간" : "주간";
		String conditionSummary = timeOfDay + "/" + skyCondition; // "야간/흐림"

		String windDirection = toKoreanWindDirection(current.getWindDeg());  // "남동풍"
		String windSpeed = formatWindSpeed(current.getWindSpeed());  // "0.4m/s"
		String wind = windDirection + " " + windSpeed; // "남동풍 0.4m/s"

		String pm10Grade = toPm10Grade(pm10); // 미세먼지 등급
		String pm25Grade = toPm25Grade(pm25); // 초미세먼지
		String uvGrade = toUvGrade(current.getUvi()); // 자외선 등급

		String sunriseTime = formatTimeWithOffset(
			current.getSunrise(),
			source.getTimezoneOffset()
		); // "05:44"

		return WeatherViewResponse.CurrentSection.builder()
			.temperature(temperature)        // "12.2°"
			.iconCode(iconCode)             // "04d"
			.iconUrl(iconUrl)               // "https://openweathermap.org/img/wn/04d@2x.png"
			.timeOfDay(timeOfDay)           // "야간"
			.skyCondition(skyCondition)     // "흐림"
			.conditionSummary(conditionSummary) // "야간/흐림"
			.feelsLike(feelsLike)           // "9.0°"
			.humidity(humidity)             // "48%"
			.windDirection(windDirection)   // "남동풍"
			.windSpeed(windSpeed)           // "0.4m/s"
			.pm10Grade(pm10Grade)           // "보통"  (미세먼지)
			.pm25Grade(pm25Grade)           // "나쁨"  (초미세먼지)
			.uvGrade(uvGrade)               // "높음"  (자외선)
			.sunriseTime(sunriseTime)       // "05:44"
			.build();
	}

	// 시간별 카드 목록 (현재부터 12시간)
	private static List<WeatherViewResponse.HourlySection> buildHourly(
		ExternalOneCallResponse source
	) {
		int offset = source.getTimezoneOffset();
		long nowEpochSecond = source.getCurrent().getDt();

		List<WeatherViewResponse.HourlySection> result = new ArrayList<>();

		// 1. hourly 리스트 꺼내기
		List<ExternalOneCallResponse.Hourly> hourlyList = source.getHourly();
		if (hourlyList == null || hourlyList.isEmpty())
			return result; //빈리스트 반환

		// 2. 현재 시각 이후의 데이터 중에서 최대 12개만 변환
		for (ExternalOneCallResponse.Hourly hourly : hourlyList) {
			if (hourly.getDt() < nowEpochSecond) {
				continue; //과거 데이터는 건너뜀
			}

			if (result.size() >= HOURLY_HOURS) {
				break; //12개 채웠으면 종료
			}

			// "03시"
			String timeLabel = formatHourLabel(hourly.getDt(), offset);

			// 시간별 온도는 정수 ("9°", 소숫점 버림)
			String temperature = formatTemperatureInt(hourly.getTemp());

			// weather 리스트에서 아이콘 꺼내기
			String iconCode = iconCodeFromWeatherList(hourly.getWeather());
			String iconUrl = toIconUrl(iconCode);

			WeatherViewResponse.HourlySection section =
				WeatherViewResponse.HourlySection.builder()
					.time(timeLabel)  // "03시"
					.temperature(temperature) // "8°"
					.iconCode(iconCode)  // "04d"
					.iconUrl(iconUrl)  // "https://openweathermap.org/img/wn/04d@2x.png"
					.build();

			result.add(section);
		}

		return result;
	}

	// 주간 예보 (오전/ 오후)
	private static List<WeatherViewResponse.DailySection> buildDaily(
		ExternalOneCallResponse source
	) {
		int offset = source.getTimezoneOffset();

		// current.dt 기준 오늘 날짜 계산
		LocalDate today = toLocalDate(source.getCurrent().getDt(), offset);

		List<WeatherViewResponse.DailySection> result = new ArrayList<>();

		// 1. daily 리스트 꺼내기
		List<ExternalOneCallResponse.Daily> dailyList = source.getDaily();

		if (dailyList == null || dailyList.isEmpty()) {
			return result; //빈리스트 반환
		}

		// 2. 오늘 포함 데이터 중에서 최대 5일치만 반환
		for (ExternalOneCallResponse.Daily daily : dailyList) {

			if (result.size() >= DAILY_DAYS) {
				break; //5일치 채웠으면 종료
			}

			LocalDate date = toLocalDate(daily.getDt(), offset);
			String dateLabel = date.format(DATE_LABEL_FORMAT); // "4.26"

			// 오늘이면 "오늘". 아니면 "월,화...,일"
			String dayOfWeek = date.equals(today)
				? "오늘"
				: toKoreanDayOfWeek(date.getDayOfWeek());

			// weather -> iconCode
			String iconCode = iconCodeFromWeatherList(daily.getWeather());
			String iconUrl = toIconUrl(iconCode);

			String amTemp;
			String pmTemp;
			if (daily.getTemp() != null) {
				amTemp = formatTemperatureInt(daily.getTemp().getMorn());
				pmTemp = formatTemperatureInt(daily.getTemp().getDay());
			} else {
				amTemp = "-";
				pmTemp = "-";
			}

			String popPercent = formatPopPercent(daily.getPop());

			WeatherViewResponse.DailySection section =
				WeatherViewResponse.DailySection.builder()
					.dayOfWeek(dayOfWeek)
					.dateLabel(dateLabel)
					.amIconCode(iconCode)
					.amIconUrl(iconUrl)
					.amTemperature(amTemp)
					.pmIconCode(iconCode)
					.pmIconUrl(iconUrl)
					.pmTemperature(pmTemp)
					.amPrecipitationProb(popPercent)
					.pmPrecipitationProb(popPercent)
					.build();

			result.add(section);
		}
		return result;
	}

	// 공통 헬퍼들

	private static String formatTemperatureDecimal(double temperature) {
		return String.format("%.1f°", temperature);
	}

	private static String formatTemperatureInt(double temperature) {
		long rounded = Math.round(temperature); // 9.2 -> 9, 9.6 -> 10
		return rounded + "°";
	}

	private static String formatWindSpeed(double windSpeed) {
		return String.format("%.1fm/s", windSpeed);
	}

	private static String skyConditionFromWeatherList(
		List<ExternalOneCallResponse.Weather> weatherList
	) {
		if (weatherList == null || weatherList.isEmpty()) {
			return "정보없음";
		} else {
			ExternalOneCallResponse.Weather first = weatherList.get(0);
			String description = first.getDescription();
			return description != null ? description : "정보없음";
		}
	}

	private static String iconCodeFromWeatherList(
		List<ExternalOneCallResponse.Weather> weatherList
	) {
		if (weatherList == null || weatherList.isEmpty()) {
			return null;
		} else {
			ExternalOneCallResponse.Weather first = weatherList.get(0);
			String iconCode = first.getIcon();
			return iconCode;
		}
	}

	private static String toIconUrl(String iconCode) {
		if (iconCode == null)
			return null;
		return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
	}

	/**
	 * 현재 시간이 일출 이전이거나 일몰 이후인지 판단
	 * @param currentDt 현재 시간
	 * @param sunriseDt 오늘 일출 시간
	 * @param sunsetDt 오늘 일몰 시간
	 * 1. currentDt < sunriseDt : 일출 이전 (밤)
	 * 2. currentDt >= sunsetDt : 일몰이후 (밤)
	 */
	private static boolean isNight(long currentDt, long sunriseDt, long sunsetDt) {
		return currentDt < sunriseDt || currentDt >= sunsetDt;
	}

	private static String toKoreanWindDirection(double deg) {
		deg = (deg % 360 + 360) % 360; // 0~360 사이로 정규화

		if (deg >= 337.5 || deg < 22.5) {
			return "북풍";
		} else if (deg >= 22.5 && deg < 67.5) {
			return "북동풍";
		} else if (deg >= 67.5 && deg < 112.5) {
			return "동풍";
		} else if (deg >= 112.5 && deg < 157.5) {
			return "남동풍";
		} else if (deg >= 157.5 && deg < 202.5) {
			return "남풍";
		} else if (deg >= 202.5 && deg < 247.5) {
			return "남서풍";
		} else if (deg >= 247.5 && deg < 292.5) {
			return "서풍";
		} else {
			return "북서풍";
		}
	}

	private static String toPm10Grade(Double pm10) {
		if (pm10 == null) {
			return "정보없음";
		} else if (pm10 <= 30) {
			return "좋음";
		} else if (pm10 <= 80) {
			return "보통";
		} else if (pm10 <= 150) {
			return "나쁨";
		} else {
			return "매우나쁨";
		}
	}

	private static String toPm25Grade(Double pm25) {
		if (pm25 == null) {
			return "정보없음";
		} else if (pm25 <= 15) {
			return "좋음";
		} else if (pm25 <= 35) {
			return "보통";
		} else if (pm25 <= 75) {
			return "나쁨";
		} else {
			return "매우나쁨";
		}
	}

	private static String toUvGrade(double uvi) {
		if (uvi <= 2) {
			return "낮음";
		} else if (uvi <= 5) {
			return "보통";
		} else if (uvi <= 7) {
			return "높음";
		} else if (uvi <= 10) {
			return "매우높음";
		} else {
			return "위험";
		}
	}

	/**
	 * epochSecond와 timezoneOffsetSeconds를 이용해 현지 시간으로 포맷팅
	 * @param epochSecond 1970-01-01 00:00:00 UTC 기준으로 몇 초 지났는지 나타내는 long 값
	 * @param timezoneOffsetSeconds UTC에서 해당 지역까지의 시차를 초 단위로 표현한 값
	 *                              예: 한국(UTC+9)은 9*3600 =32400
	 *                                  UTC-5는 -5*3600 = -18000
	 *                             		베를린(UTC+1)은 1*3600 =3600
	 * @return
	 */
	private static String formatTimeWithOffset(long epochSecond, int timezoneOffsetSeconds) {
		// Instant는 UTC 기준 절대 시각을 표현하는 타입
		// ofEpochSecond(epochSecond)는 epochSecond만큼 지난 시점을 UTC 타임라인 상의 한 점으로 만든다.
		// 이 시점에는 타임존 개념이 아직 없음 -> 그냥 전 세계 공통 기준 시간
		// epochSecond = 1732500000 이라고 치면
		// instant는 “2024-11-25T…Z” 같은 값이 됨 (Z = UTC).
		Instant instant = Instant.ofEpochSecond(epochSecond);
		// ZoneOffset은 UTC에서 몇 시간/분/초 차이냐를 나타내는타입
		// ofTotalSeconds(timezoneOffsetSeconds)는
		// 예를 들어 timezoneOffsetSeconds = 32400(=9시간)이면 -> +09:00라는 offset 객체를 만들어 준다.
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);
		//instant.atOffset(offset)
		// -> Instant + ZoneOffset -> OffsetDateTime 타입이 됨
		// -> 즉 UTC 절대 시각을 특정 시간대의 시각으로 변환한 것
		// -> instant = 2025-11-25T00:00:00Z
		// -> offset = +09:00
		// -> instant.atOffset(offset) = 2025-11-25T09:00:00+09:00
		//.toLocalTime()
		// -> OffsetDateTime에서 시간 부분만 LocalTime 타입으로 뽑아낸다.
		// -> 2025-11-25T09:00:00+09:00 -> 09:00:00
		// 즉 time은 해당 위치(오프셋 기준)의 시:분:초 정보만 가진 시간 객체
		LocalTime time = instant.atOffset(offset).toLocalTime();
		//DateTimeFormatter TIME_HH_MM_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
		// HH: 00~23시 mm: 00~59분
		// LocalTime 05:44:12 -> "05:44"
		return time.format(TIME_HH_MM_FORMAT); // "05:44"
	}

	private static String formatHourLabel(long epochSecond, int timezoneOffsetSeconds) {
		Instant instant = Instant.ofEpochSecond(epochSecond);
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);
		LocalTime time = instant.atOffset(offset).toLocalTime();
		return time.format(HOUR_LABEL_FORMAT); // "03시"
	}

	private static LocalDate toLocalDate(long epochSecond, int timezoneOffsetSeconds) {
		Instant instant = Instant.ofEpochSecond(epochSecond);
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);
		return instant.atOffset(offset).toLocalDate(); // "2024-11-25"
	}

	private static String toKoreanDayOfWeek(DayOfWeek dayOfWeek) {
		return switch (dayOfWeek) {
			case MONDAY -> "월";
			case TUESDAY -> "화";
			case WEDNESDAY -> "수";
			case THURSDAY -> "목";
			case FRIDAY -> "금";
			case SATURDAY -> "토";
			case SUNDAY -> "일";
		};
	}

	private static String formatPopPercent(double pop) {
		int percent = (int)Math.round(pop * 100);
		return percent + "%";
	}
}