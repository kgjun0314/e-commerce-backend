package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.OrderProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final StockService stockService;
    private final WalletService walletService;

    @Transactional
    public OrderResponseDto createOrder(Long memberId, List<OrderRequestDto> orderRequestDtos) {
        orderRequestDtos.sort(
                Comparator.comparing(OrderRequestDto::getProductId)
        );
        Order order = new Order();
        Wallet wallet = walletService.findByMemberIdWithLock(memberId);
        orderRequestDtos
                .forEach(orderRequestDto -> {
                    Long productId = orderRequestDto.getProductId();
                    Stock stock = stockService.findByProductIdWithLock(productId);
                    stockService.decrease(stock, orderRequestDto.getQuantity());
                    Product product = stock.getProduct();
                    OrderProduct orderProduct = new OrderProduct(product, product.getPrice(), orderRequestDto.getQuantity());
                    order.addOrderProduct(orderProduct);
                });
        Member member = wallet.getMember();
        member.addOrder(order);
        Order savedOrder = orderJpaRepository.save(order);
        walletService.decrease(wallet, savedOrder.getTotalPrice(), savedOrder.getId());
        return getOrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long id) {
//        Order order = orderJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        Order order = orderJpaRepository.findByIdFetchJoin(id);
        return getOrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(Long memberId) {
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        List<Order> orders = orderJpaRepository.findAllByMemberIdFetchJoin(memberId);
        orders.
                forEach(order -> {
                    orderResponseDtos.add(getOrderResponseDto(order));
                });
        return orderResponseDtos;
    }

    private static OrderResponseDto getOrderResponseDto(Order order) {
        List<OrderProduct> orderProducts = order.getOrderProducts();
        List<OrderProductResponseDto> orderProductResponseDtos = new ArrayList<>();
        orderProducts
                .forEach(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    OrderProductResponseDto orderProductResponseDto = new OrderProductResponseDto(
                            orderProduct.getId(),
                            product.getId(),
                            product.getName(),
                            orderProduct.getOrderPrice(),
                            orderProduct.getQuantity(),
                            orderProduct.getStatus()
                    );
                    orderProductResponseDtos.add(orderProductResponseDto);
                });
        return new OrderResponseDto(
                order.getId(),
                orderProductResponseDtos,
                order.getTotalPrice()
        );
    }
}
