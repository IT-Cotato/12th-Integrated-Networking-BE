package cotato.backend.place.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cotato.backend.common.exception.AppException;
import cotato.backend.common.exception.EntityNotFoundException;
import cotato.backend.common.exception.ErrorCode;
import cotato.backend.member.domain.Member;
import cotato.backend.member.domain.MemberRepository;
import cotato.backend.place.application.dto.PlaceCreateRequest;
import cotato.backend.place.application.dto.PlaceListResponse;
import cotato.backend.place.application.dto.PlaceResponse;
import cotato.backend.place.domain.Place;
import cotato.backend.place.infra.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

	private final MemberRepository memberRepository;
	private final PlaceRepository placeRepository;

	// SecurityContext에서 memberId 추출
	private Long getCurrentMemberId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			log.warn("인증 정보 없음: SecurityContext에 Authentication 객체가 존재하지 않습니다.");
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		try {
			Long memberId = Long.valueOf(authentication.getName());
			log.info("인증 성공: JWT에서 추출된 Member ID: {}", memberId);
			return memberId;
		} catch (NumberFormatException e) {
			log.error("토큰 값 오류: JWT Subject(memberId)를 Long으로 변환 실패. 값: {}", authentication.getName(), e);
			throw new AppException(ErrorCode.INVALID_TOKEN);
		}
	}

	// memberId 조회, 없으면 예외 발생
	private Member getMemberOrThrow(Long memberId) {
		log.debug("DB 조회 시도: Member ID {}로 Member 엔티티 조회.", memberId);
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.warn("Member 조회 실패: Member ID {}에 해당하는 엔티티 없음.", memberId);
				return new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
			});
	}

	// /locations 위치 저장
	public PlaceResponse createPlace(PlaceCreateRequest request) {
		try {
			log.info("[Place 생성 시작] 요청 데이터: placeName='{}', lat={}, lon={}, address='{}', pinned={}",
				request.getPlaceName(), request.getLatitude(), request.getLongitude(),
				request.getAddress(), request.getIsPinned());

			// 1. memberId 추출
			Long memberId = getCurrentMemberId();

			// 2. memberId로 member 조회
			Member member = getMemberOrThrow(memberId);

			// 3. 같은 장소 이름 존재 여부 확인
			log.debug("장소 생성 검증: Member ID {}의 '{}' 중복 확인 시도.", memberId, request.getPlaceName());
			if (placeRepository.existsByMemberIdAndPlaceName(memberId, request.getPlaceName())) {
				log.warn("장소 저장 실패: 이미 존재하는 장소 이름. Member ID: {}, placeName='{}'",
					memberId, request.getPlaceName());
				throw new AppException(ErrorCode.LOCATION_ALREADY_EXISTS);
			}

			// 4. Place 엔티티 생성 및 저장
			log.info("DB 저장 시도: Member ID {}의 새로운 장소 '{}' 생성. lat={}, lon={}",
				memberId, request.getPlaceName(), request.getLatitude(), request.getLongitude());

			Place newPlace = new Place(
				member,
				request.getPlaceName(),
				request.getLatitude(),
				request.getLongitude(),
				request.getAddress(),
				request.getIsPinned()
			);

			// save 시점에서 예외가 터지는지 확인하고 싶으면 saveAndFlush 써도 됨
			Place savedPlace = placeRepository.save(newPlace);
			log.info("DB 저장 성공: Place ID {} 생성 완료.", savedPlace.getId());

			return new PlaceResponse(savedPlace);
		} catch (Exception e) {
			log.error("[Place 생성 실패] 요청 placeName='{}', lat={}, lon={}, address='{}', pinned={}",
				request.getPlaceName(), request.getLatitude(), request.getLongitude(),
				request.getAddress(), request.getIsPinned(), e);
			throw e;
		}
	}

	// /locations 저장된 장소 목록 조회
	@Transactional(readOnly = true)
	public PlaceListResponse findAllPlaces() {
		try {
			Long memberId = getCurrentMemberId();
			Member member = getMemberOrThrow(memberId);

			log.info("장소 목록 조회 시도: Member ID {}의 전체 장소 목록을 DB에서 조회.", memberId);
			List<Place> places = placeRepository.findAllByMember(member);
			log.debug("조회 결과: 총 {}개의 장소 엔티티 확보.", places.size());

			List<PlaceResponse> placeResponses = places.stream()
				.map(PlaceResponse::new)
				.collect(Collectors.toList());

			return new PlaceListResponse(placeResponses);
		} catch (Exception e) {
			log.error("[Place 목록 조회 실패]", e);
			throw e;
		}
	}

	// /locations 단일 장소 조회
	@Transactional(readOnly = true)
	public PlaceResponse findPlace(Long placeId) {
		try {
			Long memberId = getCurrentMemberId();
			log.info("단일 장소 조회 시도: Place ID {}에 대해 소유권 확인 시작. 요청자 ID: {}", placeId, memberId);

			Place place = placeRepository.findById(placeId)
				.orElseThrow(() -> {
					log.warn("조회 실패: Place ID {}를 찾을 수 없음 (404 Not Found).", placeId);
					return new EntityNotFoundException(ErrorCode.LOCATION_NOT_FOUND);
				});

			if (!place.getMember().getId().equals(memberId)) {
				log.error("권한 거부: Place ID {}의 소유자는 {}이지만, 요청자는 {}입니다.",
					placeId, place.getMember().getId(), memberId);
				throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
			}

			log.info("조회 성공: Place ID {}의 소유권 확인 완료.", placeId);
			return new PlaceResponse(place);
		} catch (Exception e) {
			log.error("[단일 장소 조회 실패] placeId={}", placeId, e);
			throw e;
		}
	}

	// /locations/{locationId} 저장된 장소 삭제
	public void deletePlace(Long placeId) {
		try {
			Long memberId = getCurrentMemberId();
			log.warn("장소 삭제 시도: Place ID {}에 대해 소유권 확인 및 삭제 시작. 요청자 ID: {}", placeId, memberId);

			Place place = placeRepository.findById(placeId)
				.orElseThrow(() -> {
					log.warn("삭제 실패: Place ID {}를 찾을 수 없음 (404 Not Found).", placeId);
					return new EntityNotFoundException(ErrorCode.LOCATION_NOT_FOUND);
				});

			if (!place.getMember().getId().equals(memberId)) {
				log.error("삭제 실패: 권한 없음. Place ID {}는 요청자 소유가 아닙니다. 실제 소유자: {}, 요청자: {}",
					placeId, place.getMember().getId(), memberId);
				throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
			}

			placeRepository.delete(place);
			log.warn("DB 삭제 완료: Place ID {}가 성공적으로 삭제되었습니다.", placeId);
		} catch (Exception e) {
			log.error("[Place 삭제 실패] placeId={}", placeId, e);
			throw e;
		}
	}
}
