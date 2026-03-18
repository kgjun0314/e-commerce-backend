package com.kgj0314.e_commerce_backend.application.service.integration_test;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughBalanceException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class WalletServiceTests {
    @Autowired
    WalletService walletService;
    @Autowired
    WalletJpaRepository walletJpaRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;

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
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long orderId = 1L;
        Long price = 10000L;

        // When
        walletService.decreaseBalance(wallet, price, orderId);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(initMoney - price, walletFound.getBalance());
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
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

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
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long orderId = 1L;
        Long price = 10000L;

        // When
        walletService.increaseBalance(wallet, price, orderId);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(initMoney + price, walletFound.getBalance());
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
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        // When
        Wallet walletFound = walletService.getWalletWithLock(member.getId());

        // Then
        assertEquals(wallet, walletFound);
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
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        Long amount = 10000L;
        WalletChargeCommand walletChargeCommand = new WalletChargeCommand(amount);

        Long balanceAfter = balanceBefore + amount;

        // When
        walletService.chargeWallet(member.getId(), walletChargeCommand);

        // Then
        Wallet walletFound = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(balanceAfter, walletFound.getBalance());
    }
}
