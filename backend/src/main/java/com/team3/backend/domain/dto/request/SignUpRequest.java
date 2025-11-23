package com.team3.backend.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank
    private String email;

    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    private String password;
}