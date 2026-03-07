package com.kgj0314.e_commerce_backend.domain.order;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderedProduct> orderedProducts = new ArrayList<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @CreatedDate
    private LocalDateTime createdDate;

    public void addOrderedProduct(OrderedProduct orderedProduct) {
        orderedProduct.setOrder(this);
        orderedProducts.add(orderedProduct);
    }

    public Long getTotalPrice() {
        Long totalPrice = 0L;

        for (OrderedProduct orderedProduct : orderedProducts) {
            if (orderedProduct.getStatus() != OrderedProductStatus.CANCELED) {
                totalPrice += orderedProduct.getTotalPrice();
            }
        }

        return totalPrice;
    }
}
