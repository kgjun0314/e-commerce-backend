package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderedProductResponseDto {
    private Long orderedProductId;
    private Long productId;
    private String name;
    private Long price;
    private Long quantity;
    private OrderedProductStatus status;

    public OrderedProductResponseDto(OrderedProduct orderedProduct, Product product) {
        this.orderedProductId = orderedProduct.getId();
        this.productId = product.getId();
        this.name = product.getName();
        this.price = orderedProduct.getOrderPrice();
        this.quantity = orderedProduct.getQuantity();
        this.status = orderedProduct.getStatus();
    }
}
