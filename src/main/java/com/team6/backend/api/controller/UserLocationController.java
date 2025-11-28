package com.team6.backend.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team6.backend.api.dto.request.SaveLocationRequestDTO;
import com.team6.backend.api.dto.response.DefaultIdResponse;
import com.team6.backend.api.dto.response.UserLocationResponseDTO;
import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.userLocation.application.UserLocationService;
import com.team6.backend.domain.userLocation.dto.response.LocationPinResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/locations")
public class UserLocationController {
	private final UserLocationService userLocationService;

	@Operation(summary = "장소 저장 API")
	@PostMapping("/save")
	public ResponseEntity<DataResponse<DefaultIdResponse>> saveLocation(
		@RequestBody SaveLocationRequestDTO saveLocationRequestDTO
	) {
		Long userId = 1L; // 임시 유저 ID
		return ResponseEntity.ok(
			DataResponse.created(
				DefaultIdResponse.of(userLocationService.saveLocation(userId, saveLocationRequestDTO))
			)
		);
	}

	@Operation(summary = "장소 고정/고정 해제 API")
	@PatchMapping("/{locationId}/pin")
	public ResponseEntity<DataResponse<LocationPinResponseDTO>> pinLocation(
		@PathVariable Long locationId
	) {
		Long userId = 1L; // 임시 유저 ID
		return ResponseEntity.ok(
			DataResponse.from(
				userLocationService.pinLocation(userId, locationId)
			)
		);
	}

	@Operation(summary = "장소 삭제 API")
	@DeleteMapping("/{locationId}")
	public ResponseEntity<DataResponse<Void>> deleteLocation(
		@PathVariable Long locationId
	) {
		Long userId = 1L; // 임시 유저 ID
		userLocationService.deleteLocation(userId, locationId);
		return ResponseEntity.ok(
			DataResponse.ok()
		);
	}

	@Operation(summary = "장소 리스트 조회 API")
	@GetMapping
	public ResponseEntity<DataResponse<List<UserLocationResponseDTO>>> getLocation(

	) {
		Long userId = 1L; // 임시 유저 ID
		return ResponseEntity.ok(
			DataResponse.from(
				userLocationService.getLocation(userId)
			)
		);
	}
}
