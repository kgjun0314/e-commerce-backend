package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {
        List<Product> products = productJpaRepository.findAllWithStock();
        return products.stream()
                .map(product -> {
                    return new ProductResponseDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStock().getQuantity()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findById(Long id) {
        Product product = productJpaRepository.findByIdWithStock(id);
        return new ProductResponseDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStock().getQuantity()
        );
    }

    @Transactional
    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        Stock stock = new Stock();
        stock.setQuantity(productRequestDto.getQuantity());
        stock.setProduct(product);
        product.setStock(stock);
        productJpaRepository.save(product);

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock().getQuantity()
        );
    }
}
