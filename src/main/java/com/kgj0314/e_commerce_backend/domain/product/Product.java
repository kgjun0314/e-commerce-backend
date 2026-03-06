package com.kgj0314.e_commerce_backend.domain.product;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "product_id")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long price;

    @NotNull
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;
}
