package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.order_product.OrderProduct;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByStatus(OrderProductStatus status);

    @Query("""
        select distinct o from OrderProduct o
        join fetch o.product p
        join fetch p.stock s
        where o.status = :status
    """)
    List<OrderProduct> findByStatusFetchJoin(@Param("status") OrderProductStatus status);

    @Query("""
        select distinct o from OrderProduct o
        join fetch o.product p
        join fetch p.stock s
        where o.id = :id
    """)
    OrderProduct findByIdFetchJoin(@Param("id") Long id);
}
