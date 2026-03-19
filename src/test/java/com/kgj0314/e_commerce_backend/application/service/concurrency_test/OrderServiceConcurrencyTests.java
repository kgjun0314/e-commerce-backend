package com.kgj0314.e_commerce_backend.application.service.concurrency_test;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.service.OrderService;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
public class OrderServiceConcurrencyTests {
    @Autowired
    WalletService walletService;
    @Autowired
    WalletJpaRepository walletJpaRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    ProductJpaRepository productJpaRepository;
    @Autowired
    OrderService orderService;

    @Test
    @DisplayName("주문 생성 동시성 테스트")
    public void createOrder() throws InterruptedException {
        // Given
        long initMoney = 100000L;
        Wallet wallet = new Wallet();
        wallet.setBalance(initMoney);
        final Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        member.setWallet(wallet);
        wallet.setMember(member);
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        final Product product = new Product();
        product.setName("상품1");
        product.setPrice(100L);
        Stock stock = new Stock();
        stock.setProduct(product);
        long initQuantity = 100L;
        stock.setQuantity(initQuantity);
        product.setStock(stock);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        int threadCount = 50; // 동시에 50명 주문 시뮬레이션
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    List<OrderCommand> orderCommandList = new ArrayList<>();
                    OrderCommand orderCommand = new OrderCommand(product.getId(), 1L);
                    orderCommandList.add(orderCommand);
                    orderService.createOrder(member.getId(), orderCommandList);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Then
        Member member2 = memberJpaRepository.findById(member.getId()).orElseThrow();
        Product product2 = productJpaRepository.findById(product.getId()).orElseThrow();

        long expectedBalance = initMoney - (product.getPrice() * threadCount);
        assertEquals(expectedBalance, member2.getWallet().getBalance());

        long expectedQuantity = initQuantity - threadCount;
        assertEquals(expectedQuantity, product2.getStock().getQuantity());
    }

    @Test
    @DisplayName("재고보다 많은 주문이 동시에 들어와도 재고는 음수가 되지 않는다.")
    public void createOrder1() throws InterruptedException {
        // Given
        long initMoney = 100000L;

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

        Product product = new Product();
        product.setName("상품1");
        product.setPrice(100L);

        Stock stock = new Stock();
        stock.setProduct(product);

        long initQuantity = 30L; // 재고 30개
        stock.setQuantity(initQuantity);

        product.setStock(stock);

        productJpaRepository.save(product);
        productJpaRepository.flush();

        int threadCount = 50; // 50명이 동시에 주문
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    List<OrderCommand> orderCommandList = new ArrayList<>();
                    orderCommandList.add(new OrderCommand(product.getId(), 1L));

                    try {
                        orderService.createOrder(member.getId(), orderCommandList);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        // 재고 부족 등으로 실패
                    }

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Then
        Product product2 = productJpaRepository.findById(product.getId()).orElseThrow();
        long remainStock = product2.getStock().getQuantity();

        System.out.println(remainStock);
        // 재고는 음수가 되면 안됨
        assertTrue(remainStock >= 0);

        // 성공 주문은 최대 재고까지만
        assertEquals(initQuantity, successCount.get());

        // 최종 재고는 0
        assertEquals(0L, remainStock);
    }
}
