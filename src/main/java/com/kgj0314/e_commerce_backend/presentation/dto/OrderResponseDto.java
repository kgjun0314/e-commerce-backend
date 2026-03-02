package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private List<OrderProductResponseDto> orderedProducts;
    private Long totalPrice;
}
