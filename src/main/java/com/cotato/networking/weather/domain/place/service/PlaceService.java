package com.cotato.networking.weather.domain.place.service;

import com.cotato.networking.weather.domain.place.converter.PlaceConverter;
import com.cotato.networking.weather.domain.place.dto.request.PlaceSaveRequestDto;
import com.cotato.networking.weather.domain.place.dto.response.PlaceResponseDto;
import com.cotato.networking.weather.domain.place.entity.Place;
import com.cotato.networking.weather.domain.place.repository.PlaceRepository;
import com.cotato.networking.weather.domain.user.entity.User;
import com.cotato.networking.weather.domain.user.repository.UserRepository;
import com.cotato.networking.weather.global.exception.ErrorStatus;
import com.cotato.networking.weather.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceConverter placeConverter;

    // 장소 저장
    @Transactional
    public PlaceResponseDto create(Long userId, PlaceSaveRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Place place = placeConverter.toEntity(dto, user);
        Place savedPlace = placeRepository.save(place);

        return placeConverter.toDto(savedPlace);
    }

    // 내 장소 목록 조회
    public List<PlaceResponseDto> findAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return placeRepository.findAllByUser(user).stream()
                .map(placeConverter::toDto)
                .collect(Collectors.toList());
    }

    // 장소 삭제
    @Transactional
    public void deletePlace(Long placeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 해당 유저가 가진 장소인지 확인하며 조회 (없으면 예외 발생)
        Place place = placeRepository.findByIdAndUser(placeId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PLACE_NOT_FOUND));

        placeRepository.delete(place);
    }

    public PlaceResponseDto setPin(Long userId, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("해당 장소가 존재하지 않습니다."));

        if (!place.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 장소만 즐겨찾기 설정할 수 있습니다.");
        }

        // 이미 설정된 상태라면 그대로 반환해도 됨
        if (place.isPinned()) {
            return placeConverter.toDto(place);
        }

        place.setPinned(true);
        placeRepository.save(place);

        return placeConverter.toDto(place);
    }

    public PlaceResponseDto unsetPin(Long userId, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("해당 장소가 존재하지 않습니다."));

        if (!place.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 장소만 즐겨찾기 해제할 수 있습니다.");
        }

        if (!place.isPinned()) {
            return placeConverter.toDto(place);
        }

        place.setPinned(false);
        placeRepository.save(place);

        return placeConverter.toDto(place);
    }
}
