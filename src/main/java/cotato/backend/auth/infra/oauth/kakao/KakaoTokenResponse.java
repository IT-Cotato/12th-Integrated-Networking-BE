package cotato.backend.auth.infra.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카카오 토큰 발급 API 응답 JSON을 그대로 매핑하는 DTO
 * 인가 코드(code)를 들고 tokenUri(http://kauth.kakao.com/oauth/token)에 요청하면,
 * 카카오가 json을 주는 것을 자바 객체로 변환하는 객체
 */
@Getter
@NoArgsConstructor
@ToString
public class KakaoTokenResponse {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("expires_in")
	private Long expiresIn;

	@JsonProperty("token_type")
	private String tokenType; //ex : bearer
}
