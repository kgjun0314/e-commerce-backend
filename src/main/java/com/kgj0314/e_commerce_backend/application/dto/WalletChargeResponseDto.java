package com.kgj0314.e_commerce_backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletChargeResponseDto {
    private Long walletId;
    private Long balance;
}
