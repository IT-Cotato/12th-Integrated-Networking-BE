package com.team6.backend.domain.userLocation.application;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.backend.api.dto.request.SaveLocationRequestDTO;
import com.team6.backend.api.dto.response.UserLocationResponseDTO;
import com.team6.backend.domain.location.dao.LocationRepository;
import com.team6.backend.domain.location.entity.Location;
import com.team6.backend.domain.user.dao.UserRepository;
import com.team6.backend.domain.user.entity.User;
import com.team6.backend.domain.userLocation.dao.UserLocationRepository;
import com.team6.backend.domain.userLocation.dto.response.LocationPinResponseDTO;
import com.team6.backend.domain.userLocation.entity.UserLocation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLocationService {

	private final LocationRepository locationRepository;
	private final UserRepository userRepository;
	private final UserLocationRepository userLocationRepository;

	@Transactional
	public Long saveLocation(Long userId, SaveLocationRequestDTO saveLocationRequestDTO) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		Location location = locationRepository.save(
			Location.builder()
				.name(saveLocationRequestDTO.name())
				.lat(saveLocationRequestDTO.lat())
				.lng(saveLocationRequestDTO.lng())
				.build()
		);

		UserLocation userLocation = userLocationRepository.save(
			UserLocation.builder()
				.user(user)
				.location(location)
				.pinned(false)
				.build()
		);

		return userLocation.getUserLocationId();
	}

	@Transactional
	public LocationPinResponseDTO pinLocation(Long userId, Long locationId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("user를 찾을 수 없습니다."));

		Location location = locationRepository.findByLocationId(locationId)
			.orElseThrow(() -> new NoSuchElementException("location을 찾을 수 없습니다."));

		UserLocation userLocation = userLocationRepository.findByUserAndLocation(user, location)
			.orElseThrow(() -> new NoSuchElementException("userLocation을 찾을 수 없습니다."));

		Boolean pinned = userLocation.getPinned();

		// 이미 고정된 장소라면 고정 해제, 고정되지 않은 장소라면 고정함
		userLocation.setPinned(!pinned);

		return new LocationPinResponseDTO(location.getLocationId(), userLocation.getPinned());
	}

	@Transactional
	public void deleteLocation(Long userId, Long locationId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("user를 찾을 수 없습니다."));

		Location location = locationRepository.findByLocationId(locationId)
			.orElseThrow(() -> new NoSuchElementException("location을 찾을 수 없습니다."));

		UserLocation userLocation = userLocationRepository.findByUserAndLocation(user, location)
			.orElseThrow(() -> new NoSuchElementException("userLocation을 찾을 수 없습니다."));

		userLocationRepository.delete(userLocation);
	}

	@Transactional(readOnly = true)
	public List<UserLocationResponseDTO> getLocation(Long userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("user를 찾을 수 없습니다."));

		List<UserLocation> userLocations = userLocationRepository.findByUserOrderByPinnedDesc(user);

		return userLocations.stream()
			.map(ul -> new UserLocationResponseDTO(
				ul.getLocation().getLocationId(),
				ul.getLocation().getName(),
				ul.getLocation().getLat(),
				ul.getLocation().getLng(),
				ul.getPinned()
			))
			.toList();
	}
}
