package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.service.MemberService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.infrastructure.security.JwtUtil;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberLoginRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberLoginResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberSignupResponseDto> signup(@RequestBody MemberSignupRequestDto memberSignupRequestDto){
        MemberSignupResponseDto memberSignupResponseDto = memberService.createMember(memberSignupRequestDto);
        return ResponseEntity.ok(memberSignupResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                memberLoginRequestDto.getUsername(),
                                memberLoginRequestDto.getPassword()
                        )
                );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = null;
        if (userDetails != null) {
            token = jwtUtil.createToken(userDetails);
        }

        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto(token);

        return ResponseEntity.ok(memberLoginResponseDto);
    }
}
