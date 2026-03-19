package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.command.OrderedProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductPageDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductResponseDto;
import com.kgj0314.e_commerce_backend.application.query.OrderedProductStatusQuery;
import com.kgj0314.e_commerce_backend.application.service.OrderedProductService;
import com.kgj0314.e_commerce_backend.application.service.StockService;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.domain.exception.CannotCancellableStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.CannotChangeStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.MemberIdMismatchException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderedProductJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderedProductServiceTests {
    @Mock
    OrderedProductJpaRepository orderedProductJpaRepository;
    @Mock
    StockService stockService;
    @Mock
    WalletService walletService;
    @InjectMocks
    OrderedProductService orderedProductService;

    @Test
    @DisplayName("주문 상품을 orderedProductId로 조회할 수 있다.")
    public void getOrderedProduct() {
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

        when(orderedProductJpaRepository.findByIdFetchJoin(orderedProduct.getId())).thenReturn(orderedProduct);

        // When
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.getOrderedProduct(orderedProduct.getId());

        // Then
        assertEquals(orderedProduct.getId(), orderedProductResponseDto.getOrderedProductId());
        verify(orderedProductJpaRepository).findByIdFetchJoin(orderedProduct.getId());
    }

    @Test
    @DisplayName("주문 상품의 상태를 변경할 수 있다.")
    public void changeStatus() {
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

        when(orderedProductJpaRepository.findById(orderedProduct.getId())).thenReturn(Optional.of(orderedProduct));

        // When
        OrderedProductStatusCommand orderedProductStatusCommand;
        orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.SHIPPING);
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);

        // Then
        assertEquals(OrderedProductStatus.SHIPPING, orderedProductResponseDto.getStatus());
        verify(orderedProductJpaRepository).findById(orderedProduct.getId());

        // When
        orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.COMPLETED);
        orderedProductResponseDto = orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);

        // Then
        assertEquals(OrderedProductStatus.COMPLETED, orderedProductResponseDto.getStatus());
        verify(orderedProductJpaRepository, times(2)).findById(orderedProduct.getId());
    }

    @Test
    @DisplayName("CREATED 상태의 주문 상품을 취소할 수 있다.")
    public void cancelOrderedProduct() {
        // Given
        Wallet wallet = new Wallet();
        Member member = new Member();
        member.setId(1L);
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

        when(orderedProductJpaRepository.findById(orderedProduct.getId())).thenReturn(Optional.of(orderedProduct));
        when(stockService.getStockWithLock(product.getId())).thenReturn(stock);
        when(walletService.getWalletWithLock(member.getId())).thenReturn(wallet);

        // When
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.cancelOrderedProduct(member, orderedProduct.getId());

        // Then
        assertEquals(OrderedProductStatus.CANCELED, orderedProductResponseDto.getStatus());
        verify(orderedProductJpaRepository).findById(orderedProduct.getId());
        verify(stockService).getStockWithLock(product.getId());
        verify(walletService).getWalletWithLock(member.getId());
    }

    @Test
    @DisplayName("주문 상품들을 상태로 조회할 수 있다.")
    public void getOrderedProducts() {
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
        OrderedProductStatusQuery orderedProductStatusQuery = new OrderedProductStatusQuery(OrderedProductStatus.CREATED);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        List<OrderedProduct> list = List.of(
                orderedProduct1,
                orderedProduct2,
                orderedProduct3
        );

        Page<OrderedProduct> page = new PageImpl<>(list);

        when(orderedProductJpaRepository.findByStatusFetchJoin(orderedProductStatusQuery.getStatus(), pageable)).thenReturn(page);
        OrderedProductPageDto orderedProductPageDto = orderedProductService.getOrderedProducts(orderedProductStatusQuery, pageable);

        // Then
        assertEquals(n, orderedProductPageDto.totalElements());
        verify(orderedProductJpaRepository).findByStatusFetchJoin(orderedProductStatusQuery.getStatus(), pageable);
    }
}
