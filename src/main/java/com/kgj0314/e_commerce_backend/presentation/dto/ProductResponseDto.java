package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private Long price;
    private Long quantity;
}
