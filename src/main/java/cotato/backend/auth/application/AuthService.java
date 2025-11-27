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
		try {
			// 1. authorization code로 카카오 액세스 토큰 요청
			log.debug("1-1. Requesting Kakao access token...");
			KakaoTokenResponse tokenResponse =
				kakaoOAuthClient.requestAccessToken(authorizationCode, redirectUri);
			log.info("1. Kakao Token Response received.");
			log.debug("1-2. Token Response Details: {}", tokenResponse);

			// 2. 엑세스 토큰으로 카카오 유저 정보 조회
			log.debug("2-1. Requesting Kakao user info with access token...");
			KakaoUserInfoResponse userInfo =
				kakaoOAuthClient.requestUserInfo(tokenResponse.getAccessToken());
			log.info("2. Kakao User Info Response received.");
			log.debug("2-2. User Info Details: {}", userInfo);

			Long kakaoId = userInfo.getId();
			log.info("Kakao ID: {}", kakaoId);

			if (kakaoId == null) {
				log.error("Error: Kakao ID is null, possibly due to failed user info request.");
				throw new EntityNotFoundException(ErrorCode.KAKAO_OAUTH_FAILED);
			}

			String providerId = String.valueOf(kakaoId);
			String email = userInfo.getEmail();
			String nickname = userInfo.getNickname();
			String profileImageUrl = userInfo.getProfileImageUrl();

			log.debug("2-3. Parsed User Data: ProviderId={}, Email={}, Nickname={}, ProfileImageUrl={}",
				providerId, email, nickname, profileImageUrl);

			// 3. Member 조회/생성
			log.debug("3-0. Attempting to find existing Member by providerType={} and providerId={}",
				ProviderType.KAKAO, providerId);
			Optional<Member> existing = memberRepository.findByProviderTypeAndProviderId(ProviderType.KAKAO,
				providerId);
			log.debug("3-0-1. DB Search Result: Member exists? {}", existing.isPresent()); // <--- 이 로그로 DB 조회 직후 상태 확인

			Member member;

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

				// 신규 회원 저장 시도 전에 데이터 유효성 재확인 (DB 컬럼 제약조건 위반 방지)
				log.debug("3-2-1. New Member Data ready for save: ProviderId={}, Email={}, Nickname={}",
					newMember.getProviderId(), newMember.getEmail(), newMember.getNickname());

				member = memberRepository.save(newMember);
				log.info("3-3. 신규 회원 저장 완료 - id={}", member.getId());
			}

			log.info("3-4. 최종 Member - id={}, providerId={}", member.getId(), member.getProviderId());

			// 4. JWT 발급
			String role = "ROLE_USER";
			log.debug("4-1. Creating Access Token for Member ID: {}", member.getId());
			String accessToken = jwtTokenProvider.createAccessToken(member.getId(), role);
			String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), role);
			log.info("4. JWT Access Token issued.");
			log.debug("4-2. Access Token (start): {}", accessToken.substring(0, 10)); // 토큰 전체 대신 일부만 로깅

			// 5. 응답 DTO
			log.debug("5-1. Building KakaoLoginResponse...");
			return KakaoLoginResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.isNewUser(existing.isEmpty()) // 신규 회원 여부를 정확히 세팅
				.memberId(member.getId())
				.nickname(member.getNickname())
				.profileImageUrl(member.getProfileImageUrl())
				.build();

		} catch (EntityNotFoundException e) {
			log.error("Authentication failed due to missing entity: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			// DB 쿼리 실패, 데이터 저장 실패 등 모든 예상치 못한 런타임 예외를 여기서 잡는다.
			log.error("Critical error during Kakao login process for code {}: {}", authorizationCode, e.getMessage(),
				e);
			// 필요에 따라 Custom Exception으로 변환하거나 다시 던진다.
			throw new RuntimeException("Kakao Login failed due to internal server error.", e);
		}
	}

}