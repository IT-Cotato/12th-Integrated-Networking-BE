package cotato.backend.auth.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {
    private String authorizationCode;
    private String redirectUri;
}
