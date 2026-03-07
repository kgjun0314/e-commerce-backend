package com.kgj0314.e_commerce_backend.application.dto;

import java.util.List;

public record OrderPageDto(
        List<OrderResponseDto> contents,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) { }