package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletChargeResponseDto {
    @Schema(description = "지갑 ID", example = "1")
    private Long walletId;
    @Schema(description = "잔고", example = "10000")
    private Long balance;
    public WalletChargeResponseDto(Wallet wallet) {
        this.walletId = wallet.getId();
        this.balance = wallet.getBalance();
    }
}
