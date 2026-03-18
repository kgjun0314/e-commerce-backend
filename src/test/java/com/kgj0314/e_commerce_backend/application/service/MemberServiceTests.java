package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.MemberSignupCommand;
import com.kgj0314.e_commerce_backend.application.dto.MemberResponseDto;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class MemberServiceTests {
    @Autowired MemberService memberService;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    public void createMember() throws Exception {
        // Given
        MemberSignupCommand memberSignupCommand = new MemberSignupCommand(
                "email@mail.com",
                "Kim",
                "1234"
        );

        // When
        memberService.createMember(memberSignupCommand);

        // Then
        Member member = memberJpaRepository.findAll().get(0);
        assertEquals(memberSignupCommand.getEmail(), member.getEmail());
        assertEquals(memberSignupCommand.getUsername(), member.getUsername());
    }

    @Test
    @DisplayName("회원가입할 떄 같은 이름이 있으면 예외가 발생해야 한다.")
    public void createMember2() throws Exception {
        // Given
        MemberSignupCommand memberSignupCommand = new MemberSignupCommand(
                "email@mail.com",
                "Kim",
                "1234"
        );

        MemberSignupCommand memberSignupCommand2 = new MemberSignupCommand(
                "email2@mail.com",
                "Kim",
                "1234"
        );

        // When & Then
        memberService.createMember(memberSignupCommand);
        memberJpaRepository.flush();
        memberService.createMember(memberSignupCommand2);
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberJpaRepository.flush();
        });
    }

    @Test
    @DisplayName("회원가입할 떄 같은 이메일이 있으면 예외가 발생해야 한다.")
    public void createMember3() throws Exception {
        // Given
        MemberSignupCommand memberSignupCommand = new MemberSignupCommand(
                "email@mail.com",
                "Kim",
                "1234"
        );

        MemberSignupCommand memberSignupCommand2 = new MemberSignupCommand(
                "email@mail.com",
                "Lee",
                "1234"
        );

        // When & Then
        memberService.createMember(memberSignupCommand);
        memberJpaRepository.flush();
        memberService.createMember(memberSignupCommand2);
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberJpaRepository.flush();
        });
    }

    @Test
    @DisplayName("회원을 memberID로 조회할 수 있다.")
    public void getMember() throws Exception {
        // Given
        Member member = new Member();
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");
        memberJpaRepository.save(member);
        memberJpaRepository.flush();

        // When
        Long memberId = member.getId();
        MemberResponseDto memberResponseDto = memberService.getMember(memberId);

        // Then
        assertEquals(memberId, memberResponseDto.getId());
    }
}
