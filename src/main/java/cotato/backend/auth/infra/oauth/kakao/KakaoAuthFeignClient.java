package cotato.backend.auth.infra.oauth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//카카오 토큰 발급 서버를 호출하는 Feign 클라이언트 정의
@FeignClient(
	url = "https://kauth.kakao.com", //카카오 인증 서버의 베이스 url
	name = "kakaoAuthFeignClient"  //스프링 컨테이너에 등록될 빈 이름
)
public interface KakaoAuthFeignClient {

	// POST /oauth/token 엔드포인트 호출
	@PostMapping(
		value = "/oauth/token",                                  //베이스 url 뒤에 붙는 경로
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE     // 요청 본문 타입: x-www-form-urlencoded
	)
	KakaoTokenResponse requestAccessToken(
		@RequestParam("grant_type") String grantType,  // 필수: grant_type = authorization_code
		@RequestParam("client_id") String clientId,    // 필수: REST API Key(카카오 앱의 client_id)
		@RequestParam(value = "client_secret", required = false) String clientSecret, //선택: 보안용 secret
		@RequestParam("redirect_uri") String redirectUri, //필수: 인가 코드 받을 때 사용한 redirect_uri와 동일해야 함
		@RequestParam("code") String code                 //필수: 카카오가 리다이렉트로 넘겨준 authorization code
	);
}
