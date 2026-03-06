package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.OrderProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.query.OrderProductStatusQuery;
import com.kgj0314.e_commerce_backend.application.service.OrderProductService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.application.dto.OrderProductResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderProductStatusRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order_product")
@RestController
@RequiredArgsConstructor
public class OrderProductController {
    private final OrderProductService orderProductService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductResponseDto> getOrderProduct(@PathVariable Long id) {
        OrderProductResponseDto orderProductResponseDto = orderProductService.getOrderProduct(id);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status/{id}")
    public ResponseEntity<OrderProductResponseDto> changeStatus(@PathVariable Long id, @RequestBody OrderProductStatusRequestDto orderProductStatusRequestDto) {
        OrderProductStatusCommand orderProductStatusCommand = new OrderProductStatusCommand(orderProductStatusRequestDto.getStatus());
        OrderProductResponseDto orderProductResponseDto = orderProductService.changeStatus(id, orderProductStatusCommand);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<OrderProductResponseDto> cancelOrderProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        OrderProductResponseDto orderProductResponseDto = orderProductService.cancelOrderProduct(customUserDetails.getMember(), id);
        return ResponseEntity.ok(orderProductResponseDto);
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderProductResponseDto>> getOrderProducts(@ModelAttribute OrderProductStatusRequestDto OrderProductStatusRequestDto) {
        OrderProductStatusQuery orderProductStatusQuery = new OrderProductStatusQuery(OrderProductStatusRequestDto.getStatus());
        List<OrderProductResponseDto> orderProductResponseDtos = orderProductService.getOrderProducts(orderProductStatusQuery);
        return ResponseEntity.ok(orderProductResponseDtos);
    }
}
