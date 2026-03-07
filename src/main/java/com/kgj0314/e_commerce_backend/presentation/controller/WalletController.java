package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/wallets")
@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/charge")
    public ResponseEntity<WalletChargeResponseDto> chargeWallet(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody WalletChargeRequestDto walletChargeRequestDto) {
        WalletChargeCommand walletChargeCommand = new WalletChargeCommand(walletChargeRequestDto.getAmount());
        WalletChargeResponseDto walletChargeResponseDto = walletService.chargeWallet(customUserDetails.getMember().getId(), walletChargeCommand);
        return ResponseEntity.ok(walletChargeResponseDto);
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletResponseDto> getBalance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        WalletResponseDto walletResponseDto = walletService.getWallet(customUserDetails.getMember().getId());
        return ResponseEntity.ok(walletResponseDto);
    }
}
