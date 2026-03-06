package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts() {
        List<Product> products = productJpaRepository.findAllFetchJoin();
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
    public ProductResponseDto getProduct(Long id) {
        Product product = productJpaRepository.findByIdFetchJoin(id);
        return new ProductResponseDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getStock().getQuantity()
        );
    }

    @Transactional
    public ProductResponseDto createProduct(ProductCommand productCommand) {
        Product product = new Product();
        product.setName(productCommand.getName());
        product.setPrice(productCommand.getPrice());
        Stock stock = new Stock();
        stock.setQuantity(productCommand.getQuantity());
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
