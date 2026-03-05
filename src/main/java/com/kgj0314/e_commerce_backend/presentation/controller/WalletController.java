package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.WalletService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/wallet")
@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/charge")
    public ResponseEntity<WalletChargeResponseDto> charge(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody WalletChargeRequestDto walletChargeRequestDto) {
        WalletChargeResponseDto walletChargeResponseDto = walletService.charge(customUserDetails.getMember().getId(), walletChargeRequestDto);
        return ResponseEntity.ok(walletChargeResponseDto);
    }
}
