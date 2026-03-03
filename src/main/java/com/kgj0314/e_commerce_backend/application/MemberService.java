package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.exception.UsedEmailException;
import com.kgj0314.e_commerce_backend.domain.exception.UsedUsernameException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.infrastructure.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponseDto create(MemberRequestDto memberRequestDto) {
        Member member = new Member();
        member.setEmail(memberRequestDto.getEmail());
        member.setUsername(memberRequestDto.getUsername());
        member.setPassword(passwordEncoder.encode(memberRequestDto.getPassword()));
        member.setCreatedDate(LocalDateTime.now());
        try {
            memberJpaRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if ("members_email_key".equals(constraintName)) {
                    throw new UsedEmailException("이미 사용 중인 이메일입니다.");
                } else if ("members_username_key".equals(constraintName)) {
                    throw new UsedUsernameException("이미 사용 중인 사용자 이름입니다.");
                }
            }
        }
        return new MemberResponseDto(
                member.getEmail(),
                member.getEmail()
        );
    }
}
