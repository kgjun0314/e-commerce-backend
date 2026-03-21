package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {
    @Schema(description = "주문 ID", example = "1")
    private Long id;
    @Schema(description = "주문 상품 목록")
    private List<OrderedProductResponseDto> orderedProducts;
    @Schema(description = "주문 총액", example = "1000")
    private Long totalPrice;
    @Schema(description = "주문 생성 일시")
    private LocalDateTime createdDate;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.orderedProducts = order.getOrderedProducts().stream().map(orderedProduct ->
                new OrderedProductResponseDto(orderedProduct, orderedProduct.getProduct())
        ).toList();
        this.totalPrice = order.getTotalPrice();
        this.createdDate = order.getCreatedDate();
    }
}
