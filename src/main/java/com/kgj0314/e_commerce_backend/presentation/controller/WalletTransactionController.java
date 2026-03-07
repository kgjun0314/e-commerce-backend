package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.application.service.WalletTransactionService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
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

    @GetMapping()
    public ResponseEntity<WalletTransactionPageDto> getTransactionsPaging(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        WalletTransactionPageDto walletTransactionPageDto = walletTransactionService.getWalletTransactions(customUserDetails.getMember().getId(), pageable);
        return ResponseEntity.ok(walletTransactionPageDto);
    }
}
