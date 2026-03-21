package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.MemberSignupCommand;
import com.kgj0314.e_commerce_backend.application.service.MemberService;
import com.kgj0314.e_commerce_backend.domain.member.Role;
import com.kgj0314.e_commerce_backend.presentation.dto.MemberSignupRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "사용자 회원가입",
            description = "사용자로 회원가입합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @ApiResponse(responseCode = "409", description = "회원가입 실패: 중복 데이터 존재", content = @Content)
    @PostMapping("/as_user")
    public ResponseEntity<MemberResponseDto> signupAsUser(@RequestBody MemberSignupRequestDto memberSignupRequestDto){
        MemberSignupCommand memberSignupCommand =
                new MemberSignupCommand(
                        memberSignupRequestDto.getEmail(),
                        memberSignupRequestDto.getUsername(),
                        memberSignupRequestDto.getPassword(),
                        Role.ROLE_USER
                );
        MemberResponseDto memberResponseDto = memberService.createMember(memberSignupCommand);
        return ResponseEntity.created(URI.create("/api/members/" + memberResponseDto.getId())).body(memberResponseDto);
    }

    @Operation(
            summary = "관리자 회원가입",
            description = "관리자로 회원가입합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @ApiResponse(responseCode = "409", description = "회원가입 실패: 중복 데이터 존재", content = @Content)
    @PostMapping("/as_admin")
    public ResponseEntity<MemberResponseDto> signupAsAdmin(@RequestBody MemberSignupRequestDto memberSignupRequestDto){
        MemberSignupCommand memberSignupCommand =
                new MemberSignupCommand(
                        memberSignupRequestDto.getEmail(),
                        memberSignupRequestDto.getUsername(),
                        memberSignupRequestDto.getPassword(),
                        Role.ROLE_ADMIN
                );
        MemberResponseDto memberResponseDto = memberService.createMember(memberSignupCommand);
        return ResponseEntity.created(URI.create("/api/members/" + memberResponseDto.getId())).body(memberResponseDto);
    }

    @Operation(
            summary = "ID로 회원 조회",
            description = "memberId로 회원을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "회원 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.", content = @Content)
    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id) {
        MemberResponseDto memberResponseDto = memberService.getMember(id);
        return ResponseEntity.ok(memberResponseDto);
    }
}
