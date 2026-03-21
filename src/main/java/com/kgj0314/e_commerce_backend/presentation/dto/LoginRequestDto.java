package com.kgj0314.e_commerce_backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @Schema(description = "사용자명", example = "username")
    private String username;
    @Schema(description = "패스워드", example = "password")
    private String password;
}
