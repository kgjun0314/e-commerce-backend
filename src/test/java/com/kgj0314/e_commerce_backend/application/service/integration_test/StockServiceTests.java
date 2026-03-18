package com.kgj0314.e_commerce_backend.application.service.integration_test;

import com.kgj0314.e_commerce_backend.application.service.StockService;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughQuantityException;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.StockJpaRepository;
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
public class StockServiceTests {
    @Autowired
    ProductJpaRepository productJpaRepository;
    @Autowired
    StockService stockService;
    @Autowired
    StockJpaRepository stockJpaRepository;

    @Test
    @DisplayName("재고 > 수량일 때 재고가 정상적으로 감소한다.")
    public void decreaseQuantity(){
        // Given
        Long initQuantity = 100L;
        Stock stock = new Stock();
        stock.setQuantity(initQuantity);
        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        product.setStock(stock);
        stock.setProduct(product);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        Long quantity = 10L;

        // When
        stockService.decreaseQuantity(stock, quantity);

        // Then
        Stock stockFound = stockJpaRepository.findById(stock.getId()).orElseThrow();
        assertEquals(initQuantity - quantity, stockFound.getQuantity());
    }

    @Test
    @DisplayName("재고 < 수량일 때 예외가 발생한다.")
    public void decreaseQuantity1(){
        // Given
        Long initQuantity = 1L;
        Stock stock = new Stock();
        stock.setQuantity(initQuantity);
        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        product.setStock(stock);
        stock.setProduct(product);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        Long quantity = 10L;

        // When & Then
        assertThrows(NotEnoughQuantityException.class, () -> {
            stockService.decreaseQuantity(stock, quantity);
        });
    }

    @Test
    @DisplayName("재고가 정상적으로 증가한다.")
    public void increaseQuantity(){
        // Given
        Long initQuantity = 100L;
        Stock stock = new Stock();
        stock.setQuantity(initQuantity);
        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        product.setStock(stock);
        stock.setProduct(product);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        Long quantity = 10L;

        // When
        stockService.increaseQuantity(stock, quantity);

        // Then
        Stock stockFound = stockJpaRepository.findById(stock.getId()).orElseThrow();
        assertEquals(initQuantity + quantity, stockFound.getQuantity());
    }

    @Test
    @DisplayName("재고를 productId로 조회할 수 있다.")
    public void getStockWithLock(){
        // Given
        Long initQuantity = 100L;
        Stock stock = new Stock();
        stock.setQuantity(initQuantity);
        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        product.setStock(stock);
        stock.setProduct(product);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        // When
        Stock stockFound = stockService.getStockWithLock(product.getId());

        // Then
        assertEquals(stock, stockFound);
    }
}
