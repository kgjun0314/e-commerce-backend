package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class OrderedProductResponseDto {
    @Schema(description = "주문 상품 ID", example = "1")
    private Long orderedProductId;
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    @Schema(description = "주문 상품명", example = "상품1")
    private String name;
    @Schema(description = "주문 가격", example = "1000")
    private Long price;
    @Schema(description = "주문 수량", example = "1")
    private Long quantity;
    @Schema(description = "주문 상태", example = "CREATED")
    private OrderedProductStatus status;
    @Schema(description = "주문 일자")
    private LocalDateTime createdDate;

    public OrderedProductResponseDto(OrderedProduct orderedProduct, Product product) {
        this.orderedProductId = orderedProduct.getId();
        this.productId = product.getId();
        this.name = product.getName();
        this.price = orderedProduct.getOrderPrice();
        this.quantity = orderedProduct.getQuantity();
        this.status = orderedProduct.getStatus();
        this.createdDate = orderedProduct.getCreatedDate();
    }
}
