package com.kgj0314.e_commerce_backend.domain.ordered_product;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordered_products")
@Getter @Setter
@NoArgsConstructor
public class OrderedProduct extends BaseEntity {
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
    private OrderedProductStatus status;

    public OrderedProduct(Product product, Long orderPrice, Long quantity) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.status = OrderedProductStatus.CREATED;
    }

    public Long getTotalPrice() {
        return getOrderPrice() * getQuantity();
    }
}
