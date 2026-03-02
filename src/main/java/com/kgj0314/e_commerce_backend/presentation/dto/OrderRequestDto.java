package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.Getter;

@Getter
public class OrderRequestDto {
    private Long productId;
    private Long quantity;
}
