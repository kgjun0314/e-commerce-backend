package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.application.service.WalletTransactionService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/wallet_transactions")
@RestController
@RequiredArgsConstructor
public class WalletTransactionController {
    private final WalletTransactionService walletTransactionService;

    @Operation(
            summary = "로그인 사용자의 결제 트랜잭션 조회",
            description = "로그인한 사용자의 결제 트랜잭션을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.", content = @Content)
    @GetMapping()
    public ResponseEntity<WalletTransactionPageDto> getTransactionsPaging(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        WalletTransactionPageDto walletTransactionPageDto = walletTransactionService.getWalletTransactions(customUserDetails.getMember().getId(), pageable);
        return ResponseEntity.ok(walletTransactionPageDto);
    }
}
