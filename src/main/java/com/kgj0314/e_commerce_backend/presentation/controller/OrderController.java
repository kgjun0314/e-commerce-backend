package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.OrderService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody List<OrderRequestDto> orderRequestDtos) {
        OrderResponseDto orderResponseDto = orderService.create(customUserDetails.getMember().getId(), orderRequestDtos);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        OrderResponseDto orderResponseDto = orderService.findById(id);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDto>> getOrders(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<OrderResponseDto> orderResponseDtos = orderService.findByMemberId(customUserDetails.getMember().getId());
        return ResponseEntity.ok(orderResponseDtos);
    }
}
