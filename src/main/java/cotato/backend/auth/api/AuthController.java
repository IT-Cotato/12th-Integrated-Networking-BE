package cotato.backend.auth.api;

import cotato.backend.auth.api.dto.KakaoLoginRequest;
import cotato.backend.auth.api.dto.KakaoLoginResponse;
import cotato.backend.auth.application.AuthService;
import cotato.backend.common.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    public final AuthService authService;

    @PostMapping("/kakao/login")
    public ResponseEntity<DataResponse<KakaoLoginResponse>> kakaoLogin(@RequestBody KakaoLoginRequest request){
        System.out.println(request.getAuthorizationCode()+request.getRedirectUri());
        KakaoLoginResponse response = authService.loginWithKakao(
                    request.getAuthorizationCode(),
                    request.getRedirectUri()
                );
        return ResponseEntity.ok(DataResponse.of(response));
    }

}

