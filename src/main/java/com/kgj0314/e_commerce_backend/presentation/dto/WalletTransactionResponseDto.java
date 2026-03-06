package com.kgj0314.e_commerce_backend.presentation.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class WalletTransactionResponseDto {
    private Long walletTransactionId;

    private Long orderId;

    private Long orderProductId;

    private WalletTransactionType type;

    private Long amount;

    private Long balanceAfter;

    private LocalDateTime createdDate;
}
