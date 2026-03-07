package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.application.dto.ProductPageDto;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.ProductJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    @Transactional(readOnly = true)
    public ProductPageDto getProducts(Pageable pageable) {
        Page<Product> productPage = productJpaRepository.findAllFetchJoin(pageable);

        List<ProductResponseDto> productResponseDtoList = productPage.getContent().stream()
                .map(ProductResponseDto::new)
                .toList();

        return new ProductPageDto(
                productResponseDtoList,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Product product = productJpaRepository.findByIdFetchJoin(id);
        return new ProductResponseDto(product);
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

        return new ProductResponseDto(product);
    }
}
