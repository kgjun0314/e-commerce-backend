package com.kgj0314.e_commerce_backend.domain.stock;

import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughQuantityException;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stocks")
@Getter @Setter
public class Stock {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "stock_id")
    private Long id;

    @Column(nullable = false)
    private Long quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void increaseQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void decreaseQuantity(Long quantity) {
        if (this.quantity < quantity) {
            throw new NotEnoughQuantityException("재고가 부족합니다. (재고 ID: " + this.id + ")");
        }
        this.quantity -= quantity;
    }
}
