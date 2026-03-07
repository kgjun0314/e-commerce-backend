package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class WalletTransactionResponseDto {
    private Long walletTransactionId;
    private Long orderId;
    private Long orderedProductId;
    private WalletTransactionType type;
    private Long amount;
    private Long balanceAfter;
    private LocalDateTime createdDate;

    public WalletTransactionResponseDto(WalletTransaction walletTransaction) {
        this.walletTransactionId = walletTransaction.getId();
        this.orderId = walletTransaction.getOrderId();
        this.orderedProductId = walletTransaction.getOrderedProductId();
        this.type = walletTransaction.getType();
        this.amount = walletTransaction.getAmount();
        this.balanceAfter = walletTransaction.getBalanceAfter();
        this.createdDate = walletTransaction.getCreatedDate();
    }
}
