package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WalletResponseDto {
    @Schema(description = "잔고", example = "10000")
    private Long balance;

    public WalletResponseDto(Wallet wallet) {
        this.balance = wallet.getBalance();
    }
}
