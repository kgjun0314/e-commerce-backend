package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.OrderedProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductPageDto;
import com.kgj0314.e_commerce_backend.application.query.OrderedProductStatusQuery;
import com.kgj0314.e_commerce_backend.application.service.OrderedProductService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderedProductStatusRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/ordered_products")
@RestController
@RequiredArgsConstructor
public class OrderedProductController {
    private final OrderedProductService orderedProductService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderedProductResponseDto> getOrderedProduct(@PathVariable Long id) {
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.getOrderedProduct(id);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status/{id}")
    public ResponseEntity<OrderedProductResponseDto> changeStatus(@PathVariable Long id, @RequestBody OrderedProductStatusRequestDto orderedProductStatusRequestDto) {
        OrderedProductStatusCommand orderedProductStatusCommand = new OrderedProductStatusCommand(orderedProductStatusRequestDto.getStatus());
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.changeStatus(id, orderedProductStatusCommand);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<OrderedProductResponseDto> cancelOrderedProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.cancelOrderedProduct(customUserDetails.getMember(), id);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @GetMapping("/status")
    public ResponseEntity<OrderedProductPageDto> getOrderedProducts(@ModelAttribute OrderedProductStatusRequestDto OrderedProductStatusRequestDto, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        OrderedProductStatusQuery orderedProductStatusQuery = new OrderedProductStatusQuery(OrderedProductStatusRequestDto.getStatus());
        OrderedProductPageDto orderedProductPageDto = orderedProductService.getOrderedProducts(orderedProductStatusQuery, pageable);
        return ResponseEntity.ok(orderedProductPageDto);
    }
}
