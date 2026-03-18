package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletTransactionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class WalletTransactionServiceTests {
    @Autowired WalletTransactionService walletTransactionService;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    WalletTransactionJpaRepository walletTransactionJpaRepository;

    @Test
    @DisplayName("주문 트랜잭션을 생성할 수 있다.")
    public void createOrderTransaction() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long orderId = 1L;
        Long amount = 10000L;

        // When
        walletTransactionService.createOrderTransaction(wallet, orderId, amount);

        // Then
        WalletTransaction walletTransaction = walletTransactionJpaRepository.findAll().get(0);
        assertEquals(orderId, walletTransaction.getOrderId());
        assertEquals(WalletTransactionType.PAYMENT, walletTransaction.getType());
        assertEquals(amount, walletTransaction.getAmount());
    }

    @Test
    @DisplayName("충전 트랜잭션을 생성할 수 있다.")
    public void createChargeTransaction() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long amount = 10000L;

        // When
        walletTransactionService.createChargeTransaction(wallet, amount);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        WalletTransactionPageDto walletTransactionPageDto = walletTransactionService.getWalletTransactions(member.getId(), pageable);

        // Then
        WalletTransaction walletTransaction = walletTransactionJpaRepository.findAll().get(0);
        WalletTransactionResponseDto walletTransactionResponseDto = walletTransactionPageDto.contents().get(0);
        assertEquals(WalletTransactionType.CHARGE, walletTransactionResponseDto.getType());
        assertEquals(amount, walletTransactionResponseDto.getAmount());
    }

    @Test
    @DisplayName("환불 트랜잭션을 생성할 수 있다.")
    public void createRefundTransaction() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long orderedProductId = 1L;
        Long amount = 10000L;

        // When
        walletTransactionService.createRefundTransaction(wallet, orderedProductId, amount);

        // Then
        WalletTransaction walletTransaction = walletTransactionJpaRepository.findAll().get(0);
        assertEquals(orderedProductId, walletTransaction.getOrderedProductId());
        assertEquals(WalletTransactionType.REFUND, walletTransaction.getType());
        assertEquals(amount, walletTransaction.getAmount());
    }

    @Test
    @DisplayName("트랜잭션들을 조회할 수 있다.")
    public void getWalletTransactions() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long orderedProductId = 1L;
        Long orderId = 1L;
        Long amount = 10000L;

        // When
        walletTransactionService.createOrderTransaction(wallet, orderId, amount);
        walletTransactionService.createChargeTransaction(wallet, amount);
        walletTransactionService.createRefundTransaction(wallet, orderedProductId, amount);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        WalletTransactionPageDto walletTransactionPageDto = walletTransactionService.getWalletTransactions(member.getId(), pageable);

        // Then
        assertEquals(3, walletTransactionPageDto.totalElements());
    }
}
