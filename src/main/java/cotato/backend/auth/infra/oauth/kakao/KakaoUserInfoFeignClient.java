package cotato.backend.auth.infra.oauth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "kakaoUserInfoFeignClient",
        url = "https://kapi.kakao.com"  //유저 정보 서버
)
public interface KakaoUserInfoFeignClient {
    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(
            // Authorization: Bearer {accessToken} 헤더를 그대로 넘겨주기 위해 @RequestHeader 사용
            // KakaoOAuthClient에서 "Bearer" + kakaoAccessToken 을 붙여서 보내야함
           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
