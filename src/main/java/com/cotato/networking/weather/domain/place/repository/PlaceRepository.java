package com.cotato.networking.weather.domain.place.repository;

import com.cotato.networking.weather.domain.place.entity.Place;
import com.cotato.networking.weather.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 특정 유저의 모든 장소 조회
    List<Place> findAllByUser(User user);

    // 삭제 시 본인의 장소인지 확인하기 위해 User와 함께 조회
    Optional<Place> findByIdAndUser(Long id, User user);
}
