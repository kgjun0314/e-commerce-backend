package com.kgj0314.e_commerce_backend.presentation.dto;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderedProductStatusRequestDto {
    private OrderedProductStatus status;
}
