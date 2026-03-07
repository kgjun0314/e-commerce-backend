package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderedProductJpaRepository extends JpaRepository<OrderedProduct, Long> {
    List<OrderedProduct> findByStatus(OrderedProductStatus status);

    @Query("""
        select distinct o from OrderedProduct o
        join fetch o.product p
        join fetch p.stock s
        where o.status = :status
    """)
    List<OrderedProduct> findByStatusFetchJoin(@Param("status") OrderedProductStatus status);

    @Query("""
        select distinct o from OrderedProduct o
        join fetch o.product p
        join fetch p.stock s
        where o.id = :id
    """)
    OrderedProduct findByIdFetchJoin(@Param("id") Long id);
}
