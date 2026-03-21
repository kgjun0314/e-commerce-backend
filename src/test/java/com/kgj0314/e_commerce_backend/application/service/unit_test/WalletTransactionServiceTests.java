package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import com.kgj0314.e_commerce_backend.application.service.WalletTransactionService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.member.Role;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletTransactionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletTransactionServiceTests {
    @Mock
    WalletTransactionJpaRepository walletTransactionJpaRepository;
    @InjectMocks
    WalletTransactionService walletTransactionService;

    @Captor
    ArgumentCaptor<WalletTransaction> captor;

    @Test
    @DisplayName("주문 트랜잭션을 생성할 수 있다.")
    public void createOrderTransaction() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setRole(Role.ROLE_USER);
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderId = 1L;
        Long amount = 10000L;

        when(walletTransactionJpaRepository.save(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        walletTransactionService.createOrderTransaction(wallet, orderId, amount);

        // Then
        verify(walletTransactionJpaRepository).save(captor.capture());

        WalletTransaction walletTransaction = captor.getValue();
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
        member.setRole(Role.ROLE_USER);
        member.setWallet(wallet);
        wallet.setMember(member);

        Long amount = 10000L;

        when(walletTransactionJpaRepository.save(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        walletTransactionService.createChargeTransaction(wallet, amount);

        // Then
        verify(walletTransactionJpaRepository).save(captor.capture());

        WalletTransaction walletTransaction = captor.getValue();
        assertEquals(WalletTransactionType.CHARGE, walletTransaction.getType());
        assertEquals(amount, walletTransaction.getAmount());
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
        member.setRole(Role.ROLE_USER);
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderedProductId = 1L;
        Long amount = 10000L;

        when(walletTransactionJpaRepository.save(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        walletTransactionService.createRefundTransaction(wallet, orderedProductId, amount);

        // Then
        verify(walletTransactionJpaRepository).save(captor.capture());

        WalletTransaction walletTransaction = captor.getValue();
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
        member.setRole(Role.ROLE_USER);
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderedProductId = 1L;
        Long orderId = 1L;
        Long amount = 10000L;

        WalletTransaction walletTransaction1 = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction1);
        walletTransaction1.setOrderId(orderId);
        walletTransaction1.setType(WalletTransactionType.PAYMENT);
        walletTransaction1.setAmount(amount);
        walletTransaction1.setBalanceAfter(wallet.getBalance());

        WalletTransaction walletTransaction2 = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction2);
        walletTransaction2.setType(WalletTransactionType.CHARGE);
        walletTransaction2.setAmount(amount);
        walletTransaction2.setBalanceAfter(wallet.getBalance());

        WalletTransaction walletTransaction3 = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction3);
        walletTransaction3.setOrderedProductId(orderedProductId);
        walletTransaction3.setType(WalletTransactionType.REFUND);
        walletTransaction3.setAmount(amount);
        walletTransaction3.setBalanceAfter(wallet.getBalance());

        // When
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        List<Long> list = new ArrayList<>();
        list.add(walletTransaction1.getId());
        list.add(walletTransaction2.getId());
        list.add(walletTransaction3.getId());

        Page<Long> page = new PageImpl<>(list);

        List<WalletTransaction> walletTransactions = List.of(walletTransaction1, walletTransaction2, walletTransaction3);

        when(walletTransactionJpaRepository.findWalletTransactionIdByMemberId(member.getId(), pageable)).thenReturn(page);
        when(walletTransactionJpaRepository.findByIdListFetchJoin(page.getContent())).thenReturn(walletTransactions);
        WalletTransactionPageDto walletTransactionPageDto = walletTransactionService.getWalletTransactions(member.getId(), pageable);

        // Then
        assertEquals(3, walletTransactionPageDto.totalElements());
        verify(walletTransactionJpaRepository).findWalletTransactionIdByMemberId(member.getId(), pageable);
        verify(walletTransactionJpaRepository).findByIdListFetchJoin(page.getContent());
    }
}
