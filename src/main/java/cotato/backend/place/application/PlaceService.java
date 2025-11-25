package cotato.backend.place.application;

import cotato.backend.common.exception.AppException;
import cotato.backend.common.exception.EntityNotFoundException;
import cotato.backend.common.exception.ErrorCode;
import cotato.backend.member.domain.Member;
import cotato.backend.member.domain.MemberRepository;
import cotato.backend.place.application.dto.PlaceCreateRequest;
import cotato.backend.place.application.dto.PlaceResponse;
import cotato.backend.place.domain.Place;
import cotato.backend.place.infra.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    // memberId 조회, 없으면 예외 발생
    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // /locations 위치 저장
    public PlaceResponse createPlace(PlaceCreateRequest request) {
        Long memberId = getCurrentMemberId();

        // // 2. memberId로 member 조회
        Member member = getMemberOrThrow(memberId);

        // 3. 같은 장소 이름 존재 여부 확인
        if (placeRepository.existsByMemberIdAndPlaceName(memberId, request.getPlaceName())) {
            throw new AppException(ErrorCode.LOCATION_ALREADY_EXISTS);
        }

        // 4. Place 엔티티 생성 및 저장
        Place newPlace = new Place(
                member,
                request.getPlaceName(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAddress(),
                request.getIsPinned()
        );

        Place savedPlace = placeRepository.save(newPlace);

        return new PlaceResponse(savedPlace);
    }


}