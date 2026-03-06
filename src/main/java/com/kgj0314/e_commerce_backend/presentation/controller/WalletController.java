package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.WalletService;
import com.kgj0314.e_commerce_backend.application.WalletTransactionService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping()
    public ResponseEntity<WalletResponseDto> getBalance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        WalletResponseDto walletResponseDto = walletService.findByMemberId(customUserDetails.getMember().getId());
        return ResponseEntity.ok(walletResponseDto);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransactionResponseDto>> getTransactions(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<WalletTransactionResponseDto> WalletTransactionResponseDtos = walletService.getWalletTransactionsByMemberId(customUserDetails.getMember().getId());
        return ResponseEntity.ok(WalletTransactionResponseDtos);
    }
}
