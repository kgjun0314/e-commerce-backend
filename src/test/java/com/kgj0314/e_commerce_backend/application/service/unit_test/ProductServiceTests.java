package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.application.dto.ProductPageDto;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import com.kgj0314.e_commerce_backend.application.service.ProductService;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    ProductJpaRepository productJpaRepository;
    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    public void createProduct(){
        // Given
        String name = "상품1";
        Long price = 10000L;
        Long quantity = 100L;
        ProductCommand productCommand = new ProductCommand("상품1", 10000L, quantity);

        when(productJpaRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductResponseDto productResponseDto = productService.createProduct(productCommand);

        // Then
        assertEquals(name, productResponseDto.getName());
        assertEquals(price, productResponseDto.getPrice());
        assertEquals(price, productResponseDto.getPrice());
        verify(productJpaRepository).save(any(Product.class));
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

        when(productJpaRepository.findByIdFetchJoin(product.getId())).thenReturn(product);

        // When
        ProductResponseDto productResponseDto = productService.getProduct(product.getId());

        // Then
        assertEquals(product.getName(), productResponseDto.getName());
        assertEquals(product.getPrice(), productResponseDto.getPrice());
        verify(productJpaRepository).findByIdFetchJoin(product.getId());
    }

    @Test
    @DisplayName("전체 상품을 조회할 수 있다.")
    public void getProducts(){
        // Given
        long n = 3;

        Product product1 = new Product();
        product1.setName("상품1");
        product1.setPrice(10000L);
        Stock stock1 = new Stock();
        stock1.setProduct(product1);
        stock1.setQuantity(0L);
        product1.setStock(stock1);

        Product product2 = new Product();
        product2.setName("상품2");
        product2.setPrice(10000L);
        Stock stock2 = new Stock();
        stock2.setProduct(product2);
        stock2.setQuantity(0L);
        product2.setStock(stock2);

        Product product3 = new Product();
        product1.setName("상품3");
        product1.setPrice(10000L);
        Stock stock3 = new Stock();
        stock3.setProduct(product3);
        stock3.setQuantity(0L);
        product3.setStock(stock3);

        // When
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        List<Product> list = List.of(product1, product2, product3);
        Page<Product> page = new PageImpl<>(list);

        when(productJpaRepository.findAllFetchJoin(pageable)).thenReturn(page);

        ProductPageDto ProductPageDto = productService.getProducts(pageable);

        // Then
        assertEquals(n, ProductPageDto.totalElements());
        verify(productJpaRepository).findAllFetchJoin(pageable);
    }
}
