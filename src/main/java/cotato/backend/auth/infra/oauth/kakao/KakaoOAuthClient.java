package cotato.backend.auth.infra.oauth.kakao;

import org.springframework.stereotype.Component;

import cotato.backend.common.exception.AppException;
import cotato.backend.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

	private final KakaoOAuthProperties properties;
	private final KakaoAuthFeignClient kakaoAuthFeignClient;
	private final KakaoUserInfoFeignClient kakaoUserInfoFeignClient;

	public KakaoTokenResponse requestAccessToken(String authorizationCode, String redirectUriFromClient) {
		//프론트에서 redirectUri 안 보내면 yml에 있는 기본값 사용
		String redirectUri =
			(redirectUriFromClient != null && !redirectUriFromClient.isBlank()) ? redirectUriFromClient :
				properties.getRedirectUri();
		log.info("Requesting Kakao access token with authorization code: {}", authorizationCode);
		log.info("Kakao OAuth Properties - Client ID: {}, Client Secret: {}, Redirect URI: {}",
			properties.getClientId(),
			properties.getClientSecret(),
			redirectUri
		);
		try {
			KakaoTokenResponse response = kakaoAuthFeignClient.requestAccessToken(
				"authorization_code",
				properties.getClientId(),
				properties.getClientSecret(),
				redirectUri,
				authorizationCode
			);
			log.info("Kakao Token Response received: {}", response);

			if (response == null || response.getAccessToken() == null) {
				throw new AppException(ErrorCode.KAKAO_OAUTH_FAILED);
			}
			return response;

		} catch (FeignException e) {
			// 여기서 카카오가 던진 400/401/500 응답 바디를 볼 수 있음
			log.error("카카오 토큰 요청 실패 - status: {}, body: {}",
				e.status(),
				e.contentUTF8(),   // 카카오가 준 JSON 에러 응답
				e
			);
			throw new AppException(ErrorCode.KAKAO_OAUTH_FAILED);
		}

	}

	public KakaoUserInfoResponse requestUserInfo(String accessToken) {
		String authorizationHeader = "Bearer " + accessToken;

		log.info("[KakaoOAuthClient] Requesting Kakao user info with accessToken: {}", accessToken);

		try {
			KakaoUserInfoResponse response = kakaoUserInfoFeignClient.getUserInfo(authorizationHeader);

			log.info("[KakaoOAuthClient] Kakao User Info Response received: {}", response);

			if (response == null) {
				log.error("[KakaoOAuthClient] Kakao user info response is null");
				throw new AppException(ErrorCode.KAKAO_OAUTH_FAILED);
			}

			return response;

		} catch (FeignException e) {
			String body = null;
			try {
				body = e.contentUTF8();
			} catch (Exception ignored) {
			}

			log.error(
				"[KakaoOAuthClient] Failed to request Kakao user info. status: {}, body: {}",
				e.status(), body, e
			);
			throw new AppException(ErrorCode.KAKAO_OAUTH_FAILED);
		}

	}

}
