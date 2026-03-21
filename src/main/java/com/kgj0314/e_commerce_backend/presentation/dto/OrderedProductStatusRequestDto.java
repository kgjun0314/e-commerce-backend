package com.kgj0314.e_commerce_backend.presentation.dto;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderedProductStatusRequestDto {
    @Schema(description = "상태", example = "CREATED")
    private OrderedProductStatus status;
}
