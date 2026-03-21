package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderPageDto;
import com.kgj0314.e_commerce_backend.application.service.OrderService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderRequestDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "주문을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @ApiResponse(responseCode = "400", description = "주문 실패")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody List<OrderRequestDto> orderRequestDtoList) {
        List<OrderCommand> orderCommandList = orderRequestDtoList
                .stream()
                .map(orderRequestDto -> new OrderCommand(orderRequestDto.getProductId(), orderRequestDto.getQuantity()))
                .collect(Collectors.toList());
        OrderResponseDto orderResponseDto = orderService.createOrder(customUserDetails.getMember().getId(), orderCommandList);
        return ResponseEntity.created(URI.create("/api/orders/" + orderResponseDto.getId())).body(orderResponseDto);
    }

    @Operation(
            summary = "주문 조회",
            description = "orderId로 주문을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        OrderResponseDto orderResponseDto = orderService.getOrder(id);
        return ResponseEntity.ok(orderResponseDto);
    }

    @Operation(
            summary = "로그인 사용자의 주문 내역 조회",
            description = "로그인한 사용자의 주문 내역을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @GetMapping()
    public ResponseEntity<OrderPageDto> getOrdersPaging(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        OrderPageDto orderPageDto = orderService.getOrders(customUserDetails.getMember().getId(), pageable);
        return ResponseEntity.ok(orderPageDto);
    }
}
