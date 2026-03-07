package com.kgj0314.e_commerce_backend.domain.wallet;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallet_transactions")
@Getter @Setter
public class WalletTransaction extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "wallet_transaction_id")
    private Long id;

    private Long orderId;

    private Long orderedProductId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WalletTransactionType type;

    @NotNull
    private Long amount;

    @NotNull
    private Long balanceAfter;
}
