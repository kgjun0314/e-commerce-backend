package com.kgj0314.e_commerce_backend.domain.order_product;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long orderPrice;

    @Column(nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
