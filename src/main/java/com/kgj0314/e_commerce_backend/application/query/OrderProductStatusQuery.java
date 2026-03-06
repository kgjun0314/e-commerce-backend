package com.kgj0314.e_commerce_backend.application.query;

import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OrderProductStatusQuery {
    private OrderProductStatus status;
}
