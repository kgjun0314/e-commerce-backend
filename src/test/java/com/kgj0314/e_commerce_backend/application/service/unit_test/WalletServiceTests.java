package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.application.service.WalletTransactionService;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughBalanceException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class WalletServiceTests {
    @Mock
    WalletJpaRepository walletJpaRepository;
    @Mock
    WalletTransactionService walletTransactionService;
    @InjectMocks
    WalletService walletService;

    @Test
    @DisplayName("잔고 > 가격일 때 잔고가 정상적으로 감소한다.")
    public void decreaseBalance(){
        // Given
        Long initMoney = 50000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(initMoney);
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderId = 1L;
        Long price = 10000L;

        when(walletJpaRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        // When
        walletService.decreaseBalance(wallet, price, orderId);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(initMoney - price, walletFound.getBalance());
        verify(walletJpaRepository).findById(wallet.getId());
    }

    @Test
    @DisplayName("잔고 < 가격일때 예외가 발생한다.")
    public void decreaseBalance1(){
        // Given
        Long initMoney = 5000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(initMoney);
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderId = 1L;
        Long price = 10000L;

        // When & Then
        assertThrows(NotEnoughBalanceException.class, () -> {
            walletService.decreaseBalance(wallet, price, orderId);
        });
    }

    @Test
    @DisplayName("잔고가 정상적으로 증가한다.")
    public void increaseBalance(){
        // Given
        Long initMoney = 50000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(initMoney);
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);

        Long orderId = 1L;
        Long price = 10000L;

        when(walletJpaRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        // When
        walletService.increaseBalance(wallet, price, orderId);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(initMoney + price, walletFound.getBalance());
        verify(walletJpaRepository).findById(wallet.getId());
    }

    @Test
    @DisplayName("memberId로 지갑을 조회할 수 있다.")
    public void getWallet(){
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);

        when(walletJpaRepository.findByMemberIdWithLock(member.getId())).thenReturn(Optional.of(wallet));

        // When
        Wallet walletFound = walletService.getWalletWithLock(member.getId());

        // Then
        assertEquals(wallet, walletFound);
        verify(walletJpaRepository).findByMemberIdWithLock(member.getId());
    }

    @Test
    @DisplayName("잔고를 충전할 수 있다.")
    public void chargeWallet(){
        // Given
        Long balanceBefore = 50000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(balanceBefore);
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);

        Long amount = 10000L;
        WalletChargeCommand walletChargeCommand = new WalletChargeCommand(amount);

        Long balanceAfter = balanceBefore + amount;

        when(walletJpaRepository.findByMemberIdWithLock(member.getId())).thenReturn(Optional.of(wallet));
        when(walletJpaRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        // When
        walletService.chargeWallet(member.getId(), walletChargeCommand);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(balanceAfter, walletFound.getBalance());
        verify(walletJpaRepository).findByMemberIdWithLock(member.getId());
        verify(walletJpaRepository).findById(wallet.getId());
    }
}
