package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderedProductJpaRepository extends JpaRepository<OrderedProduct, Long> {
    List<OrderedProduct> findByStatus(OrderedProductStatus status);

    @Query("""
        SELECT DISTINCT o FROM OrderedProduct o
        JOIN FETCH o.product p
        JOIN FETCH p.stock s
        WHERE o.status = :status
        ORDER BY o.createdDate DESC
    """)
    Page<OrderedProduct> findByStatusFetchJoin(@Param("status") OrderedProductStatus status, Pageable pageable);

    @Query("""
        SELECT DISTINCT o FROM OrderedProduct o
        JOIN FETCH o.product p
        JOIN FETCH p.stock s
        WHERE o.id = :id
    """)
    OrderedProduct findByIdFetchJoin(@Param("id") Long id);
}
