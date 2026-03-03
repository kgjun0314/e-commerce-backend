package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.MemberService;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberSignupResponseDto> signup(@RequestBody MemberSignupRequestDto memberSignupRequestDto){
        MemberSignupResponseDto memberSignupResponseDto = memberService.create(memberSignupRequestDto);
        return ResponseEntity.ok(memberSignupResponseDto);
    }
}
