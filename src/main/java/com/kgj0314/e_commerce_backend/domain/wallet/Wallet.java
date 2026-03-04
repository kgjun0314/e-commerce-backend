package com.kgj0314.e_commerce_backend.domain.wallet;

import com.kgj0314.e_commerce_backend.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@Getter @Setter
public class Wallet {
    @Id @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long balance;

    public void increase(Long price) {
        this.balance += price;
    }

    public void decrease(Long price) {
        this.balance -= price;
    }
}
