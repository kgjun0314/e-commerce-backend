package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT o.id FROM Order o
        WHERE o.member.id = :memberId
        ORDER BY o.createdDate DESC
    """)
    Page<Long> findOrderIdByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.orderedProducts op
        JOIN FETCH op.product p
        JOIN FETCH p.stock
        WHERE o.id in :orderIds
        ORDER BY o.createdDate DESC
    """)
    List<Order> findByIdListFetchJoin(@Param("orderIds") List<Long> orderIds);

    @Query("""
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.orderedProducts op
        JOIN FETCH op.product p
        JOIN FETCH p.stock
        WHERE o.id = :id
    """)
    Order findByIdFetchJoin(@Param("id") Long id);
}
