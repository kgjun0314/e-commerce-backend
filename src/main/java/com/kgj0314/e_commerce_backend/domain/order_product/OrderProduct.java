package com.kgj0314.e_commerce_backend.domain.order_product;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_products")
@Getter @Setter
@NoArgsConstructor
public class OrderProduct {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_product_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private Long orderPrice;

    @NotNull
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderProductStatus status;

    public OrderProduct(Product product, Long orderPrice, Long quantity) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.status = OrderProductStatus.CREATED;
    }

    public Long getTotalPrice() {
        return getOrderPrice() * getQuantity();
    }
}
