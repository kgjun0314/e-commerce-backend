package com.kgj0314.e_commerce_backend.application.service.integration_test;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class WalletServiceConcurrencyTests {
    @Autowired
    WalletService walletService;
    @Autowired
    WalletJpaRepository walletJpaRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("잔고 충전 동시성 테스트")
    public void chargeWallet() throws InterruptedException {
        // Given
        long balanceBefore = 50000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(balanceBefore);
        Member member = new Member();
        member.setEmail("email2@mail.com");
        member.setUsername("Kim");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        int threadCount = 50;
        long amount = 10000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    walletService.chargeWallet(member.getId(), new WalletChargeCommand(amount));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Then
        Wallet walletAfter = walletJpaRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(balanceBefore + (amount * threadCount), walletAfter.getBalance());
    }
}
