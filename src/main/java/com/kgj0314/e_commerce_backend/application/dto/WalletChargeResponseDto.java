package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletChargeResponseDto {
    private Long walletId;
    private Long balance;
    public WalletChargeResponseDto(Wallet wallet) {
        this.walletId = wallet.getId();
        this.balance = wallet.getBalance();
    }
}
