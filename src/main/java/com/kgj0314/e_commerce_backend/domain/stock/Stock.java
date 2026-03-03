package com.kgj0314.e_commerce_backend.domain.stock;

import com.kgj0314.e_commerce_backend.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stocks")
@Getter @Setter
public class Stock {
    @Id @GeneratedValue
    @Column(name = "stock_id")
    private Long id;

    @Column(nullable = false)
    private Long quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void increase(Long quantity) {
        this.quantity += quantity;
    }

    public void decrease(Long quantity) {
        this.quantity -= quantity;
    }
}
