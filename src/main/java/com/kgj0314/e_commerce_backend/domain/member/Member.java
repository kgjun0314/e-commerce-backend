package com.kgj0314.e_commerce_backend.domain.member;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter @Setter
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member() {
        this.role = Role.ROLE_USER;
    }
}
