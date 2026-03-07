package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.service.OrderService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody List<OrderRequestDto> orderRequestDtoList) {
        List<OrderCommand> orderCommands = new ArrayList<>();
        orderRequestDtoList
                .forEach(orderRequestDto -> {
                    OrderCommand orderCommand = new OrderCommand(orderRequestDto.getProductId(), orderRequestDto.getQuantity());
                    orderCommands.add(orderCommand);
                });
        OrderResponseDto orderResponseDto = orderService.createOrder(customUserDetails.getMember().getId(), orderCommands);
        return ResponseEntity.created(URI.create("/api/orders/" + orderResponseDto.getId())).body(orderResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        OrderResponseDto orderResponseDto = orderService.getOrder(id);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDto>> getOrders(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<OrderResponseDto> orderResponseDtoList = orderService.getOrders(customUserDetails.getMember().getId());
        return ResponseEntity.ok(orderResponseDtoList);
    }
}
