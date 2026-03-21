package com.kgj0314.e_commerce_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record WalletTransactionPageDto(
        List<WalletTransactionResponseDto> contents,
        @Schema(description = "총 데이터 수", example = "1")
        long totalElements,
        @Schema(description = "총 페이지 수", example = "1")
        int totalPages,
        @Schema(description = "현재 페이지 수", example = "0")
        int pageNumber,
        @Schema(description = "페이지 사이즈", example = "10")
        int pageSize
) {
}
