package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.application.service.WalletService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/wallets")
@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @Operation(
            summary = "로그인 사용자의 잔고 충전",
            description = "로그인한 사용자의 잔고를 충전합니다."
    )
    @ApiResponse(responseCode = "200", description = "잔고 충전 성공")
    @ApiResponse(responseCode = "400", description = "잔고 충전 실패")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @PatchMapping("/charge")
    public ResponseEntity<WalletChargeResponseDto> chargeWallet(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody WalletChargeRequestDto walletChargeRequestDto) {
        WalletChargeCommand walletChargeCommand = new WalletChargeCommand(walletChargeRequestDto.getAmount());
        WalletChargeResponseDto walletChargeResponseDto = walletService.chargeWallet(customUserDetails.getMember().getId(), walletChargeCommand);
        return ResponseEntity.ok(walletChargeResponseDto);
    }

    @Operation(
            summary = "로그인 사용자의 잔고 조회",
            description = "로그인한 사용자의 잔고를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "잔고 충전 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @GetMapping("/balance")
    public ResponseEntity<WalletResponseDto> getBalance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        WalletResponseDto walletResponseDto = walletService.getWallet(customUserDetails.getMember().getId());
        return ResponseEntity.ok(walletResponseDto);
    }
}
