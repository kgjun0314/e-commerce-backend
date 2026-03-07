package com.kgj0314.e_commerce_backend.infrastructure.persistence;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletJpaRepository extends JpaRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT w FROM Wallet w
        JOIN FETCH w.member m
        WHERE w.member.id = :memberId
    """)
    Optional<Wallet> findByMemberIdWithLock(@Param("memberId") Long memberId);

    Wallet findByMemberId(Long memberId);
}
