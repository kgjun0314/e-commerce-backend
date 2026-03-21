package com.kgj0314.e_commerce_backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class WalletChargeRequestDto {
    @Schema(description = "충전 금액", example = "10000")
    private Long amount;
}
