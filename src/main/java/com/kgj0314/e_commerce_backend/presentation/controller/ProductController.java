package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.ProductService;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderProductResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public List<ProductResponseDto> findProducts() {
        return productService.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.create(productRequestDto);
        return ResponseEntity.ok(productResponseDto);
    }
}
