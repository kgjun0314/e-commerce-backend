package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransaction, Long> {
    @Query("""
        SELECT t.id FROM WalletTransaction t
        WHERE t.wallet.member.id = :memberId
        ORDER BY t.createdDate DESC
    """)
    Page<Long> findWalletTransactionIdByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
        SELECT t FROM WalletTransaction t
        JOIN FETCH t.wallet w
        WHERE t.id IN :transactionIds
        ORDER BY t.createdDate DESC
    """)
    List<WalletTransaction> findByIdListFetchJoin(@Param("transactionIds") List<Long> transactionIds);
}
