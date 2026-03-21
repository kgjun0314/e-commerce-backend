package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class WalletTransactionResponseDto {
    @Schema(description = "트랜잭션 ID", example = "1")
    private Long walletTransactionId;
    @Schema(description = "주문 ID", example = "1")
    private Long orderId;
    @Schema(description = "주문 상품 ID", example = "1")
    private Long orderedProductId;
    @Schema(description = "트랜잭션 종류", example = "CHARGE")
    private WalletTransactionType type;
    @Schema(description = "금액", example = "1000")
    private Long amount;
    @Schema(description = "트랜잭션 이후 잔고", example = "11000")
    private Long balanceAfter;
    @Schema(description = "발생 일시")
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
