package cotato.backend.place.infra.repository;

import cotato.backend.member.domain.Member;
import cotato.backend.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByMember(Member member);
    boolean existsByMemberIdAndPlaceName(Long memberId, String placeName);
}
