package com.kgj0314.e_commerce_backend.infrastructure.security;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberJpaRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        return new CustomUserDetails(member);
    }
}
