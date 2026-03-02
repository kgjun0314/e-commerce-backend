package com.kgj0314.e_commerce_backend.presentation.dto;

import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductStatusRequestDto {
    private OrderProductStatus status;
}
