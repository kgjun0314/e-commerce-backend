package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.MemberSignupCommand;
import com.kgj0314.e_commerce_backend.application.service.MemberService;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberSignupRequestDto memberSignupRequestDto){
        MemberSignupCommand memberSignupCommand =
                new MemberSignupCommand(
                        memberSignupRequestDto.getEmail(),
                        memberSignupRequestDto.getUsername(),
                        memberSignupRequestDto.getPassword()
                );
        MemberResponseDto memberResponseDto = memberService.createMember(memberSignupCommand);
        return ResponseEntity.created(URI.create("/api/members/" + memberResponseDto.getId())).body(memberResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id) {
        MemberResponseDto memberResponseDto = memberService.getMember(id);
        return ResponseEntity.ok(memberResponseDto);
    }
}
