package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderedProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductPageDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductResponseDto;
import com.kgj0314.e_commerce_backend.application.query.OrderedProductStatusQuery;
import com.kgj0314.e_commerce_backend.domain.exception.CannotCancellableStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.CannotChangeStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.MemberIdMismatchException;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughQuantityException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class OrderedProductServiceTests {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    ProductJpaRepository productJpaRepository;
    @Autowired
    OrderJpaRepository orderJpaRepository;
    @Autowired
    OrderedProductService orderedProductService;
    @Autowired
    OrderedProductJpaRepository orderedProductJpaRepository;

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
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.getOrderedProduct(orderedProduct.getId());

        // Then
        assertEquals(orderedProduct.getId(), orderedProductResponseDto.getOrderedProductId());
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
        OrderedProductStatusCommand orderedProductStatusCommand;
        orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.SHIPPING);
        orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);

        // Then
        OrderedProduct orderedProductFound = orderedProductJpaRepository.findById(orderedProduct.getId()).orElseThrow();
        assertEquals(OrderedProductStatus.SHIPPING, orderedProductFound.getStatus());

        // When
        orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.COMPLETED);
        orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);

        // Then
        orderedProductFound = orderedProductJpaRepository.findById(orderedProduct.getId()).orElseThrow();
        assertEquals(OrderedProductStatus.COMPLETED, orderedProductFound.getStatus());
    }

    @Test
    @DisplayName("주문 상품의 상태를 변경할 때, 변경 전과 동일한 상태로 변경하려고 하면 예외가 발생한다.")
    public void changeStatus1() {
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

        // When & Then
        OrderedProductStatusCommand orderedProductStatusCommand;
        orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.CREATED);
        assertThrows(CannotChangeStatusException.class, () -> {
            orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);
        });
    }

    @Test
    @DisplayName("완료 상태인 주문 상태는 변경할 수 없다.")
    public void changeStatus2() {
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

        OrderedProductStatusCommand orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.COMPLETED);
        orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);

        // When & Then
        OrderedProductStatusCommand orderedProductStatusCommand2 = new OrderedProductStatusCommand(OrderedProductStatus.SHIPPING);
        assertThrows(CannotChangeStatusException.class, () -> {
            orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand2);
        });
    }

    @Test
    @DisplayName("CREATED 상태의 주문 상품을 취소할 수 있다.")
    public void cancelOrderedProduct() {
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
        orderedProductService.cancelOrderedProduct(member, orderedProduct.getId());

        // Then
        OrderedProduct orderedProductFound = orderedProductJpaRepository.findById(orderedProduct.getId()).orElseThrow();
        assertEquals(OrderedProductStatus.CANCELED, orderedProductFound.getStatus());
    }

    @Test
    @DisplayName("타인의 주문 상품을 취소할 수 없다.")
    public void cancelOrderedProduct1() {
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

        Wallet wallet2 = new Wallet();
        Member member2 = new Member();
        member2.setEmail("email2@mail.com");
        member2.setUsername("Lee2");
        member2.setPassword("1234");
        member2.setWallet(wallet2);
        wallet2.setMember(member2);
        memberJpaRepository.save(member2);
        memberJpaRepository.flush();

        // When & Then
        assertThrows(MemberIdMismatchException.class, () -> {
            orderedProductService.cancelOrderedProduct(member2, orderedProduct.getId());
        });
    }

    @Test
    @DisplayName("주문 상태 변경 메서드로 취소는 불가능하다.")
    public void changeStatus3() {
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

        // When & Then
        OrderedProductStatusCommand orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.CANCELED);
        assertThrows(CannotCancellableStatusException.class, () -> {
            orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);
        });
    }

    @Test
    @DisplayName("이미 취소된 주문 상품의 상태는 변경할 수 없다.")
    public void changeStatus4() {
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

        orderedProductService.cancelOrderedProduct(member, orderedProduct.getId());

        // When & Then
        OrderedProductStatusCommand orderedProductStatusCommand = new OrderedProductStatusCommand(OrderedProductStatus.CREATED);
        assertThrows(CannotChangeStatusException.class, () -> {
            orderedProductService.changeStatus(orderedProduct.getId(), orderedProductStatusCommand);
        });
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

        for (long i = 0; i < n; i++) {
            OrderedProduct orderedProduct = new OrderedProduct(product, 10L);
            Order order = new Order();
            order.setMember(member);
            order.addOrderedProduct(orderedProduct);
            orderJpaRepository.save(order);
            orderJpaRepository.flush();
        }

        // When
        OrderedProductStatusQuery orderedProductStatusQuery = new OrderedProductStatusQuery(OrderedProductStatus.CREATED);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        OrderedProductPageDto orderedProductPageDto = orderedProductService.getOrderedProducts(orderedProductStatusQuery, pageable);

        // Then
        assertEquals(n, orderedProductPageDto.totalElements());
    }
}
