package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.ProductCommand;
import com.kgj0314.e_commerce_backend.application.dto.ProductPageDto;
import com.kgj0314.e_commerce_backend.application.service.ProductService;
import com.kgj0314.e_commerce_backend.presentation.dto.ProductRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.ProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "상품 리스트 조회",
            description = "상품 리스트를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "상품 리스트 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @GetMapping()
    public ResponseEntity<ProductPageDto> getProducts(@ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        ProductPageDto productPageDto = productService.getProducts(pageable);
        return ResponseEntity.ok(productPageDto);
    }

    @Operation(
            summary = "상품 조회",
            description = "productId로 상품을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto productResponseDto = productService.getProduct(id);
        return ResponseEntity.ok(productResponseDto);
    }

    @Operation(
            summary = "상품 생성",
            description = "상품을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "상품 생성 성공")
    @ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
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
