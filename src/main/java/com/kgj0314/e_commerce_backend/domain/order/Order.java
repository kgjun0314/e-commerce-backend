package com.kgj0314.e_commerce_backend.domain.order;

import com.kgj0314.e_commerce_backend.domain.BaseEntity;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProduct;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProduct.setOrder(this);
        orderProducts.add(orderProduct);
    }

    public Long getTotalPrice() {
        Long totalPrice = 0L;

        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getStatus() != OrderProductStatus.CANCELED) {
                totalPrice += orderProduct.getTotalPrice();
            }
        }

        return totalPrice;
    }
}
