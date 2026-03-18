package com.kgj0314.e_commerce_backend.application.service.integration_test;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderPageDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import com.kgj0314.e_commerce_backend.application.service.OrderService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class OrderServiceTests {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    ProductJpaRepository productJpaRepository;
    @Autowired
    OrderJpaRepository orderJpaRepository;
    @Autowired
    OrderService orderService;

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    public void createOrder() {
        // Given
        Wallet wallet = new Wallet();
        wallet.setBalance(50000L);
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
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);
        product.setStock(stock);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        // When
        List<OrderCommand> orderCommandList = new ArrayList<>();
        OrderCommand orderCommand = new OrderCommand(product.getId(), 1L);
        orderCommandList.add(orderCommand);
        orderService.createOrder(member.getId(), orderCommandList);

        // Then
        Order order = orderJpaRepository.findAll().get(0);
        assertEquals(product, order.getOrderedProducts().get(0).getProduct());
    }

    @Test
    @DisplayName("주문을 orderId로 조회할 수 있다.")
    public void getOrder() {
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

        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);
        product.setStock(stock);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        OrderedProduct orderedProduct = new OrderedProduct(product, 10L);
        Order order = new Order();
        order.setMember(member);
        order.addOrderedProduct(orderedProduct);
        orderJpaRepository.save(order);
        orderJpaRepository.flush();

        // When
        OrderResponseDto orderResponseDto = orderService.getOrder(order.getId());

        // Then
        assertEquals(order.getId(), orderResponseDto.getId());
    }

    @Test
    @DisplayName("memberId를 가진 회원의 전체 주문을 조회할 수 있다.")
    public void getOrders() {
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

        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);
        product.setStock(stock);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        long n = 3;
        for(long i = 0; i < n; i++) {
            OrderedProduct orderedProduct = new OrderedProduct(product, 10L);
            Order order = new Order();
            order.setMember(member);
            order.addOrderedProduct(orderedProduct);
            orderJpaRepository.save(order);
            orderJpaRepository.flush();
        }

        // When
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        OrderPageDto orderPageDto = orderService.getOrders(member.getId(), pageable);

        // Then
        assertEquals(n, orderPageDto.totalElements());
    }
}
