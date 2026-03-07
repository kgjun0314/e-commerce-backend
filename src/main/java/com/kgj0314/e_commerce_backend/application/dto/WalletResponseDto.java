package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WalletResponseDto {
    private Long balance;

    public WalletResponseDto(Wallet wallet) {
        this.balance = wallet.getBalance();
    }
}
