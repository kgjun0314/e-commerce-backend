package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private List<OrderedProductResponseDto> orderedProducts;
    private Long totalPrice;
    private LocalDateTime createdDate;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        List<OrderedProduct> orderedProductList = order.getOrderedProducts();
        List<OrderedProductResponseDto> orderedProductResponseDtoList = new ArrayList<>();
        orderedProductList
                .forEach(orderedProduct -> {
                    Product product = orderedProduct.getProduct();
                    orderedProductResponseDtoList.add(new OrderedProductResponseDto(orderedProduct, product));
                });
        this.orderedProducts = orderedProductResponseDtoList;
        this.totalPrice = order.getTotalPrice();
        this.createdDate = order.getCreatedDate();
    }
}
