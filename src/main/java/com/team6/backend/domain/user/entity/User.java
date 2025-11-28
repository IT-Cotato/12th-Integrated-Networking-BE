package com.team6.backend.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.team6.backend.domain.userLocation.entity.UserLocation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long userId;

	private String email;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserLocation> userLocations = new ArrayList<>();
}
