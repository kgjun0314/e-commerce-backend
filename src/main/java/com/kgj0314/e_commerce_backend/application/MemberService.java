package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.exception.UsedEmailException;
import com.kgj0314.e_commerce_backend.domain.exception.UsedUsernameException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.member.Role;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupResponseDto;
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

    public MemberSignupResponseDto create(MemberSignupRequestDto memberSignupRequestDto) {
        Member member = new Member();
        member.setEmail(memberSignupRequestDto.getEmail());
        member.setUsername(memberSignupRequestDto.getUsername());
        member.setPassword(passwordEncoder.encode(memberSignupRequestDto.getPassword()));
        Wallet wallet = new Wallet();
        wallet.setMember(member);
        member.setWallet(wallet);
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
        return new MemberSignupResponseDto(
                member.getEmail(),
                member.getEmail()
        );
    }

    public Member findById(Long id) {
        return memberJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
    }
}
