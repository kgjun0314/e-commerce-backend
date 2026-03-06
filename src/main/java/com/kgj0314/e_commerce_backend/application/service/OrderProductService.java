package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderProductResponseDto;
import com.kgj0314.e_commerce_backend.application.query.OrderProductStatusQuery;
import com.kgj0314.e_commerce_backend.domain.exception.CannotCancellableStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.CannotChangeStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.exception.MemberIdMismatchException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProduct;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProductStatus;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductService {
    private final OrderProductJpaRepository orderProductJpaRepository;
    private final StockService stockService;
    private final OrderService orderService;
    private final WalletService walletService;

    @Transactional(readOnly = true)
    public OrderProductResponseDto getOrderProduct(Long id) {
//        OrderProduct orderProduct = orderProductJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문 정보입니다."));
        OrderProduct orderProduct = orderProductJpaRepository.findByIdFetchJoin(id);
        Product product = orderProduct.getProduct();
        return getOrderProductResponseDto(orderProduct, product);
    }

    @Transactional
    public OrderProductResponseDto changeStatus(Long id, OrderProductStatusCommand orderProductStatusCommand) {
        OrderProductStatus nextStatus = orderProductStatusCommand.getStatus();
        if(nextStatus == OrderProductStatus.CANCELED) {
            throw new CannotCancellableStatusException("주문 취소 API를 호출하세요.");
        }
        OrderProduct orderProduct = orderProductJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        OrderProductStatus currentStatus = orderProduct.getStatus();

        if(nextStatus == currentStatus) {
            throw new CannotChangeStatusException("변경 전과 변경 후가 동일합니다.");
        }
        else if(currentStatus == OrderProductStatus.CANCELED) {
            throw new CannotChangeStatusException("이미 취소된 주문입니다.");
        }

        orderProduct.setStatus(nextStatus);
        return getOrderProductResponseDto(orderProduct, orderProduct.getProduct());
    }

    @Transactional
    public OrderProductResponseDto cancelOrderProduct(Member member, Long id) {
        OrderProduct orderProduct = orderProductJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문 정보입니다."));
        if(!member.getId().equals(orderProduct.getOrder().getMember().getId())) {
            throw new MemberIdMismatchException("리소스의 소유자와 로그인 사용자가 서로 다릅니다.");
        }
        OrderProductStatus status = orderProduct.getStatus();
        status.checkCancellable();
        Product product = orderProduct.getProduct();
        Stock stock = stockService.getStockWithLock(product.getId());
        stockService.increaseStock(stock, orderProduct.getQuantity());
        Wallet wallet = walletService.getWalletWithLock(member.getId());
        walletService.increaseBalance(wallet, orderProduct.getTotalPrice(), orderProduct.getId());
        orderProduct.setStatus(OrderProductStatus.CANCELED);
        return getOrderProductResponseDto(orderProduct, product);
    }

    @Transactional
    public List<OrderProductResponseDto> getOrderProducts(OrderProductStatusQuery orderProductStatusQuery) {
        OrderProductStatus status = orderProductStatusQuery.getStatus();
        List<OrderProduct> orderProductList = orderProductJpaRepository.findByStatusFetchJoin(status);
        List<OrderProductResponseDto> orderProductResponseDtos = new ArrayList<>();
        orderProductList
                .forEach(orderProduct -> {
                    OrderProductResponseDto orderProductResponseDto = getOrderProductResponseDto(orderProduct, orderProduct.getProduct());
                    orderProductResponseDtos.add(orderProductResponseDto);
                });
        return orderProductResponseDtos;
    }

    private static OrderProductResponseDto getOrderProductResponseDto(OrderProduct orderProduct, Product product) {
        return new OrderProductResponseDto(
                orderProduct.getProduct().getId(),
                product.getId(),
                product.getName(),
                orderProduct.getOrderPrice(),
                orderProduct.getQuantity(),
                orderProduct.getStatus()
        );
    }
}
