package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderPageDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import com.kgj0314.e_commerce_backend.application.service.OrderService;
import com.kgj0314.e_commerce_backend.application.service.StockService;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.StockJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {
    @Mock
    OrderJpaRepository orderJpaRepository;
    @Mock
    StockJpaRepository stockJpaRepository;
    @Mock
    WalletService walletService;
    @Mock
    StockService stockService;
    @InjectMocks
    OrderService orderService;

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createOrder() {
        // Given
        Long memberId = 1L;
        Long productId = 10L;

        Product product = new Product();
        product.setId(productId);
        product.setPrice(10000L);

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);

        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setId(memberId);
        wallet.setMember(member);

        List<OrderCommand> commands = List.of(
                new OrderCommand(productId, 2L)
        );

        when(stockJpaRepository.findByProductIdListWithLock(any())).thenReturn(List.of(stock));
        when(walletService.getWalletWithLock(memberId)).thenReturn(wallet);
        when(orderJpaRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderResponseDto orderResponseDto = orderService.createOrder(memberId, commands);

        // Then
        assertEquals(1, orderResponseDto.getOrderedProducts().size());

        verify(stockJpaRepository).findByProductIdListWithLock(any());
        verify(walletService).getWalletWithLock(memberId);
        verify(orderJpaRepository).save(any(Order.class));
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

        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);
        product.setStock(stock);

        OrderedProduct orderedProduct = new OrderedProduct(product, 10L);
        Order order = new Order();
        order.setMember(member);
        order.addOrderedProduct(orderedProduct);

        when(orderJpaRepository.findByIdFetchJoin(order.getId())).thenReturn(order);

        // When
        OrderResponseDto orderResponseDto = orderService.getOrder(order.getId());

        // Then
        assertEquals(order.getId(), orderResponseDto.getId());
        verify(orderJpaRepository).findByIdFetchJoin(order.getId());
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

        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(100L);
        product.setStock(stock);

        long n = 3;
        OrderedProduct orderedProduct1 = new OrderedProduct(product, 10L);
        Order order1 = new Order();
        order1.setMember(member);
        order1.addOrderedProduct(orderedProduct1);

        OrderedProduct orderedProduct2 = new OrderedProduct(product, 10L);
        Order order2 = new Order();
        order2.setMember(member);
        order2.addOrderedProduct(orderedProduct2);

        OrderedProduct orderedProduct3 = new OrderedProduct(product, 10L);
        Order order3 = new Order();
        order3.setMember(member);
        order3.addOrderedProduct(orderedProduct3);

        // When
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        List<Long> list = new ArrayList<>();
        list.add(order1.getId());
        list.add(order2.getId());
        list.add(order3.getId());

        Page<Long> page = new PageImpl<>(list);

        List<Order> orders = List.of(order1, order2, order3);

        when(orderJpaRepository.findOrderIdByMemberId(member.getId(), pageable)).thenReturn(page);
        when(orderJpaRepository.findByIdListFetchJoin(page.getContent())).thenReturn(orders);
        OrderPageDto orderPageDto = orderService.getOrders(member.getId(), pageable);

        // Then
        assertEquals(n, orderPageDto.totalElements());
        verify(orderJpaRepository).findOrderIdByMemberId(member.getId(), pageable);
        verify(orderJpaRepository).findByIdListFetchJoin(page.getContent());
    }
}
