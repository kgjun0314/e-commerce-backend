package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberId(Long memberId);

    @Query("""
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.orderProducts op
        JOIN FETCH op.product p
        JOIN FETCH p.stock
        WHERE o.member.id = :memberId
    """)
    List<Order> findAllByMemberIdFetchJoin(@Param("memberId") Long memberId);

    @Query("""
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.orderProducts op
        JOIN FETCH op.product p
        JOIN FETCH p.stock
        WHERE o.id = :id
    """)
    Order findByIdFetchJoin(@Param("id") Long id);
}
