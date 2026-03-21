package com.kgj0314.e_commerce_backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @Schema(description = "상품명", example = "상품1")
    private String name;
    @Schema(description = "가격", example = "1000")
    private Long price;
    @Schema(description = "수량", example = "100")
    private Long quantity;
}
