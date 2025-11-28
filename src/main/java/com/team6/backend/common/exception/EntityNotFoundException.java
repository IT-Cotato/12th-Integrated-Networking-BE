package com.team6.backend.common.exception;

public class EntityNotFoundException extends AppException {

	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}