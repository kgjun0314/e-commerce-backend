package com.kgj0314.e_commerce_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    @Schema(description = "엑세스 토큰", example = "ey...")
    private String token;
}
