package com.kgj0314.e_commerce_backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
    @Schema(description = "이메일", example = "email@mail.com")
    private String email;
    @Schema(description = "아이디", example = "username")
    private String username;
    @Schema(description = "패스워드", example = "password")
    private String password;
}
