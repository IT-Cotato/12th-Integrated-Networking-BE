package com.team6.backend.api.dto.response;

public record UserLocationResponseDTO(
	Long locationId, String name, Double lat, Double lng, Boolean pinned
) {
}
