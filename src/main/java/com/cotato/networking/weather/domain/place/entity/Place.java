package com.cotato.networking.weather.domain.place.entity;

import com.cotato.networking.weather.domain.user.entity.User;
import com.cotato.networking.weather.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Place extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String placeName;

	private String addressName;

	private String roadAddressName;

	private String longitude; // x (경도)

	private String latitude; // y (위도)

	private boolean isPinned = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}

