package com.team6.backend.api.dto.request;

public record SaveLocationRequestDTO(
	String name, Double lat, Double lng
) {
}
