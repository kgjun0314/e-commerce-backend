package com.kgj0314.e_commerce_backend.application.dto;

import java.util.List;

public record WalletTransactionPageDto(
        List<WalletTransactionResponseDto> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {
}
