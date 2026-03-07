package com.kgj0314.e_commerce_backend.application.query;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderedProductStatusQuery {
    private OrderedProductStatus status;
}
