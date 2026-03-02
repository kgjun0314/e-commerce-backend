package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.Getter;

@Getter
public class ProductRequestDto {
    private String name;
    private Long price;
    private Long quantity;
}
