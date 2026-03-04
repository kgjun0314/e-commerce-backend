package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.OrderProductService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderProductResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderProductStatusRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order_product")
@RestController
@RequiredArgsConstructor
public class OrderProductController {
    private final OrderProductService orderProductService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductResponseDto> getById(@PathVariable Long id) {
        OrderProductResponseDto orderProductResponseDto = orderProductService.findById(id);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<OrderProductResponseDto> changeStatus(@PathVariable Long id, @RequestBody OrderProductStatusRequestDto orderProductStatusRequestDto) {
        OrderProductResponseDto orderProductResponseDto = orderProductService.changeStatus(id, orderProductStatusRequestDto);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<OrderProductResponseDto> cancel(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        OrderProductResponseDto orderProductResponseDto = orderProductService.cancel(customUserDetails.getMember(), id);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderProductResponseDto>> getByStatus(@ModelAttribute OrderProductStatusRequestDto OrderProductStatusRequestDto) {
        List<OrderProductResponseDto> orderProductResponseDtos = orderProductService.findByStatus(OrderProductStatusRequestDto);
        return ResponseEntity.ok(orderProductResponseDtos);
    }
}
