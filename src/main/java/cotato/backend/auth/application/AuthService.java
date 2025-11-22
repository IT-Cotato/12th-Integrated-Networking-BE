package cotato.backend.auth.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import cotato.backend.auth.api.dto.KakaoLoginResponse;
import cotato.backend.auth.infra.oauth.kakao.KakaoOAuthClient;
import cotato.backend.auth.infra.oauth.kakao.KakaoTokenResponse;
import cotato.backend.auth.infra.oauth.kakao.KakaoUserInfoResponse;
import cotato.backend.common.exception.EntityNotFoundException;
import cotato.backend.common.exception.ErrorCode;
import cotato.backend.global.security.jwt.JwtTokenProvider;
import cotato.backend.member.domain.Member;
import cotato.backend.member.domain.MemberRepository;
import cotato.backend.member.domain.ProviderType;
import cotato.backend.member.domain.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoOAuthClient kakaoOAuthClient;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public KakaoLoginResponse loginWithKakao(String authorizationCode, String redirectUri) {
		log.info("Starting Kakao login process with authorization code: {}", authorizationCode);
		// 1. authorization code로 카카오 액세스 토큰 요청
		KakaoTokenResponse tokenResponse =
			kakaoOAuthClient.requestAccessToken(authorizationCode, redirectUri);
		log.info("1. Kakao Token Response: {}", tokenResponse);

		// 2. 엑세스 토큰으로 카카오 유저 정보 조회
		KakaoUserInfoResponse userInfo =
			kakaoOAuthClient.requestUserInfo(tokenResponse.getAccessToken());
		log.info("2. Kakao User Info Response: {}", userInfo);

		Long kakaoId = userInfo.getId();
		log.info("Kakao ID: {}", kakaoId);
		if (kakaoId == null) {
			throw new EntityNotFoundException(ErrorCode.KAKAO_OAUTH_FAILED);
		}
		String providerId = String.valueOf(kakaoId);
		String email = userInfo.getEmail();
		String nickname = userInfo.getNickname();
		String profileImageUrl = userInfo.getProfileImageUrl();

		// 3. Member 조회/생성 (만약 추가 정보를 입력받는 경우 분리하여 클라에게 추가 입력 정보 다시 받기)
		Member member;
		Optional<Member> existing = memberRepository.findByProviderTypeAndProviderId(ProviderType.KAKAO, providerId);

		if (existing.isPresent()) {
			member = existing.get();
			member.updateProfile(nickname, profileImageUrl);
			log.info("3-1. 기존 회원 로그인 - id={}", member.getId());
		} else {
			log.info("3-2. 신규 회원 생성");
			Member newMember = Member.builder()
				.providerType(ProviderType.KAKAO)
				.providerId(providerId)
				.email(email)
				.nickname(nickname)
				.profileImageUrl(profileImageUrl)
				.role(Role.USER)
				.build();
			member = memberRepository.save(newMember);
			log.info("3-3. 신규 회원 저장 완료 - id={}", member.getId());
		}

		log.info("3-4. 최종 Member - id={}, providerId={}", member.getId(), member.getProviderId());

		// 4. JWT 발급
		String role = "ROLE_USER";
		String accessToken = jwtTokenProvider.createAccessToken(member.getId(), role);
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), role);
		log.info("4. JWT Access Token: {}", accessToken);

		// 5. 응답 DTO
		return KakaoLoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.isNewUser(false) // 필요하면 로직 분리해서 세팅
			.memberId(member.getId())
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.build();

	}

}

