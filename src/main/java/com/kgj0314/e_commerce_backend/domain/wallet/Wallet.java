package com.kgj0314.e_commerce_backend.domain.wallet;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughBalanceException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@Getter @Setter
public class Wallet extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "wallet_id")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private Long balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<WalletTransaction> transactions = new ArrayList<>();

    public Wallet() {
        this.balance = 0L;
    }

    public void increaseBalance(Long price) {
        this.balance += price;
    }

    public void decreaseBalance(Long price) {
        this.balance -= price;
    }

    public void addWalletTransaction(WalletTransaction walletTransaction) {
        this.transactions.add(walletTransaction);
        walletTransaction.setWallet(this);
    }
}
