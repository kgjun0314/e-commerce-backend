package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.application.dto.ProductPageDto;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class ProductServiceTests {
    @Autowired ProductService productService;
    @Autowired
    ProductJpaRepository productJpaRepository;

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    public void createProduct(){
        // Given
        String name = "상품1";
        Long price = 10000L;
        Long quantity = 100L;
        ProductCommand productCommand = new ProductCommand("상품1", 10000L, quantity);

        // When
        productService.createProduct(productCommand);

        // Then
        Product product = productJpaRepository.findAll().get(0);
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(price, product.getPrice());
    }

    @Test
    @DisplayName("상품을 productId로 조회할 수 있다.")
    public void getProduct(){
        // Given
        Product product = new Product();
        product.setName("상품1");
        product.setPrice(10000L);
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(0L);
        product.setStock(stock);
        productJpaRepository.save(product);
        productJpaRepository.flush();

        // When
        ProductResponseDto productResponseDto = productService.getProduct(product.getId());

        // Then
        assertEquals(product.getName(), productResponseDto.getName());
        assertEquals(product.getPrice(), productResponseDto.getPrice());
    }

    @Test
    @DisplayName("전체 상품을 조회할 수 있다.")
    public void getProducts(){
        // Given
        long n = 3;
        for (long i = 0; i < n; i++) {
            Product product = new Product();
            product.setName("상품1");
            product.setPrice(10000L);
            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setQuantity(0L);
            product.setStock(stock);
            productJpaRepository.save(product);
            productJpaRepository.flush();
        }

        // When
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        ProductPageDto ProductPageDto = productService.getProducts(pageable);

        // Then
        assertEquals(n, ProductPageDto.totalElements());
    }
}
