package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.dto.LoginResponseDto;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.infrastructure.security.JwtUtil;
import com.kgj0314.e_commerce_backend.presentation.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getUsername(),
                                loginRequestDto.getPassword()
                        )
                );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = null;
        if (userDetails != null) {
            token = jwtUtil.createToken(userDetails);
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(token);

        return ResponseEntity.ok(loginResponseDto);
    }
}
