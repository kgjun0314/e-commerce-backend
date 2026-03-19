package com.kgj0314.e_commerce_backend.application.service.unit_test;

import com.kgj0314.e_commerce_backend.application.command.MemberSignupCommand;
import com.kgj0314.e_commerce_backend.application.dto.MemberResponseDto;
import com.kgj0314.e_commerce_backend.application.service.MemberService;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @InjectMocks
    MemberService memberService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
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

        Member savedMember = new Member();
        savedMember.setEmail(memberSignupCommand.getEmail());
        savedMember.setUsername(memberSignupCommand.getUsername());
        savedMember.setPassword("encoded1234");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded1234");
        when(memberJpaRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MemberResponseDto memberResponseDto = memberService.createMember(memberSignupCommand);

        // Then
        assertEquals(memberSignupCommand.getEmail(), memberResponseDto.getEmail());
        assertEquals(memberSignupCommand.getUsername(), memberResponseDto.getUsername());
        verify(memberJpaRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원을 memberID로 조회할 수 있다.")
    public void getMember() throws Exception {
        // Given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setEmail("email@mail.com");
        member.setUsername("Lee");
        member.setPassword("1234");

        when(memberJpaRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        MemberResponseDto memberResponseDto = memberService.getMember(memberId);

        // Then
        assertEquals(memberId, memberResponseDto.getId());
        assertEquals("email@mail.com", memberResponseDto.getEmail());
        assertEquals("Lee", memberResponseDto.getUsername());
        verify(memberJpaRepository).findById(memberId);
    }
}
