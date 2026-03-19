package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.service.StockService;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughQuantityException;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTests {
    @Mock
    StockJpaRepository stockJpaRepository;
    @InjectMocks
    StockService stockService;

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

        Long quantity = 10L;

        when(stockJpaRepository.findById(stock.getId())).thenReturn(Optional.of(stock));

        // When
        stockService.decreaseQuantity(stock, quantity);

        // Then
        Stock stockFound = stockJpaRepository.findById(stock.getId()).orElseThrow();
        assertEquals(initQuantity - quantity, stockFound.getQuantity());
        verify(stockJpaRepository).findById(stock.getId());
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

        Long quantity = 10L;

        when(stockJpaRepository.findById(stock.getId())).thenReturn(Optional.of(stock));

        // When
        stockService.increaseQuantity(stock, quantity);

        // Then
        Stock stockFound = stockJpaRepository.findById(stock.getId()).orElseThrow();
        assertEquals(initQuantity + quantity, stockFound.getQuantity());
        verify(stockJpaRepository).findById(stock.getId());
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

        when(stockJpaRepository.findByProductIdWithLock(product.getId())).thenReturn(Optional.of(stock));

        // When
        Stock stockFound = stockService.getStockWithLock(product.getId());

        // Then
        assertEquals(stock, stockFound);
        verify(stockJpaRepository).findByProductIdWithLock(product.getId());
    }
}
