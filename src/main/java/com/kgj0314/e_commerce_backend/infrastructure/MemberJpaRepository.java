package com.kgj0314.e_commerce_backend.infrastructure;

import com.kgj0314.e_commerce_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
