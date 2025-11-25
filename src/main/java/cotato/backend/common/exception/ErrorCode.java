package cotato.backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//400
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "COMMON-001"),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "요청 파라미터가 잘못되었습니다.", "COMMON-002"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다.", "COMMON-003"),
	//500
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 에러가 발생하였습니다.", "COMMON-005"),

	// 카카오 OAUTH 관련 에러
	KAKAO_OAUTH_FAILED(HttpStatus.BAD_GATEWAY, "카카오 OAuth 토큰 발급에 실패했습니다.", "KAKAO_001"),

	//AUTJ/JWT
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.", "AUTH-001"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", "AUTH-002"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "AUTH-003"),
	FORBIDDEN( HttpStatus.FORBIDDEN, "접근 권한이 없습니다.","AUTH-004"),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.", "MEMBER_001"),

    // Location
    LOCATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 저장된 장소입니다.", "LOCATION-001"),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다.", "LOCARION-002")

    ;

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}