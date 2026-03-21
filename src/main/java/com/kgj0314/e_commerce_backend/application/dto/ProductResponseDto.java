package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    @Schema(description = "상품명", example = "상품1")
    private String name;
    @Schema(description = "가격", example = "1000")
    private Long price;
    @Schema(description = "재고", example = "100")
    private Long quantity;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getStock().getQuantity();
    }
}
