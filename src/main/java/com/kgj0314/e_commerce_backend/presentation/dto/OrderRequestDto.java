package com.kgj0314.e_commerce_backend.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class OrderRequestDto {
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    @Schema(description = "수량", example = "1")
    private Long quantity;
}
