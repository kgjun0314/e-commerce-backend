package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderProductResponseDto {
    private Long orderProductId;
    private Long productId;
    private String name;
    private Long price;
    private Long quantity;
    private OrderProductStatus status;
}
