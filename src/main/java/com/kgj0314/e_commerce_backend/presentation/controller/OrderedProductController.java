package com.kgj0314.e_commerce_backend.presentation.controller;

import com.kgj0314.e_commerce_backend.application.command.OrderedProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductPageDto;
import com.kgj0314.e_commerce_backend.application.query.OrderedProductStatusQuery;
import com.kgj0314.e_commerce_backend.application.service.OrderedProductService;
import com.kgj0314.e_commerce_backend.infrastructure.security.CustomUserDetails;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderedProductStatusRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/ordered_products")
@RestController
@RequiredArgsConstructor
public class OrderedProductController {
    private final OrderedProductService orderedProductService;

    @Operation(
            summary = "주문 상품 조회",
            description = "orderedProductId로 주문 상품을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상품 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 주문 상품입니다.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderedProductResponseDto> getOrderedProduct(@PathVariable Long id) {
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.getOrderedProduct(id);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @Operation(
            summary = "주문 상품 상태 변경",
            description = "주문 상품의 상태를 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상품 상태 변경 성공")
    @ApiResponse(responseCode = "400", description = "상태 변경 실패")
    @ApiResponse(responseCode = "403", description = "관리자 권한 필요합니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 주문 상품입니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status/{id}")
    public ResponseEntity<OrderedProductResponseDto> changeStatus(@PathVariable Long id, @RequestBody OrderedProductStatusRequestDto orderedProductStatusRequestDto) {
        OrderedProductStatusCommand orderedProductStatusCommand = new OrderedProductStatusCommand(orderedProductStatusRequestDto.getStatus());
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.changeStatus(id, orderedProductStatusCommand);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @Operation(
            summary = "주문 상품 취소",
            description = "주문 상품을 취소합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상품 취소 성공")
    @ApiResponse(responseCode = "400", description = "취소 실패")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 주문 상품입니다.")
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<OrderedProductResponseDto> cancelOrderedProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        OrderedProductResponseDto orderedProductResponseDto = orderedProductService.cancelOrderedProduct(customUserDetails.getMember(), id);
        return ResponseEntity.ok(orderedProductResponseDto);
    }

    @Operation(
            summary = "주문 상품을 상태 별로 조회",
            description = "주문 상품을 상태 별로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상품 조회 성공")
    @ApiResponse(responseCode = "403", description = "로그인이 필요합니다.")
    @GetMapping("/status")
    public ResponseEntity<OrderedProductPageDto> getOrderedProducts(@ModelAttribute OrderedProductStatusRequestDto OrderedProductStatusRequestDto, @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        OrderedProductStatusQuery orderedProductStatusQuery = new OrderedProductStatusQuery(OrderedProductStatusRequestDto.getStatus());
        OrderedProductPageDto orderedProductPageDto = orderedProductService.getOrderedProducts(orderedProductStatusQuery, pageable);
        return ResponseEntity.ok(orderedProductPageDto);
    }
}
