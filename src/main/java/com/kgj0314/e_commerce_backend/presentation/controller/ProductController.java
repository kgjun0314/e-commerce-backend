package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.ProductService;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public List<ProductResponseDto> getProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.create(productRequestDto);
        return ResponseEntity.ok(productResponseDto);
    }
}
