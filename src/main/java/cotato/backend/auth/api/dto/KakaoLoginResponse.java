package cotato.backend.auth.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class KakaoLoginResponse {
	private String accessToken;
	private String refreshToken;
	private boolean isNewUser;
	private Long memberId;
	private String nickname;
	private String profileImageUrl;
}
