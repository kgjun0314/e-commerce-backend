package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.application.dto.ProductPageDto;
import com.kgj0314.e_commerce_backend.application.service.ProductService;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<ProductPageDto> getProducts(@PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        ProductPageDto productPageDto = productService.getProducts(pageable);
        return ResponseEntity.ok(productPageDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto productResponseDto = productService.getProduct(id);
        return ResponseEntity.ok(productResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductCommand productCommand
                = new ProductCommand(
                productRequestDto.getName(),
                productRequestDto.getPrice(),
                productRequestDto.getQuantity());
        ProductResponseDto productResponseDto = productService.createProduct(productCommand);
        return ResponseEntity.created(URI.create("/api/products/" + productResponseDto.getId())).body(productResponseDto);
    }
}
