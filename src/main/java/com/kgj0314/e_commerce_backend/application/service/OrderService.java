package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.order_product.OrderProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.OrderProductResponseDto;
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
    public OrderResponseDto createOrder(Long memberId, List<OrderCommand> orderCommandList) {
        orderCommandList.sort(
                Comparator.comparing(OrderCommand::getProductId)
        );
        Order order = new Order();
        Wallet wallet = walletService.getWalletWithLock(memberId);
        orderCommandList
                .forEach(orderCommand -> {
                    Long productId = orderCommand.getProductId();
                    Stock stock = stockService.getStockWithLock(productId);
                    stockService.decreaseStock(stock, orderCommand.getQuantity());
                    Product product = stock.getProduct();
                    OrderProduct orderProduct = new OrderProduct(product, product.getPrice(), orderCommand.getQuantity());
                    order.addOrderProduct(orderProduct);
                });
        Member member = wallet.getMember();
        member.addOrder(order);
        Order savedOrder = orderJpaRepository.save(order);
        walletService.decreaseBalance(wallet, savedOrder.getTotalPrice(), savedOrder.getId());
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
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        List<Order> orderList = orderJpaRepository.findByMemberIdFetchJoin(memberId);
        orderList.
                forEach(order -> {
                    orderResponseDtoList.add(getOrderResponseDto(order));
                });
        return orderResponseDtoList;
    }

    private static OrderResponseDto getOrderResponseDto(Order order) {
        List<OrderProduct> orderProductList = order.getOrderProducts();
        List<OrderProductResponseDto> orderProductResponseDtoList = new ArrayList<>();
        orderProductList
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
                    orderProductResponseDtoList.add(orderProductResponseDto);
                });
        return new OrderResponseDto(
                order.getId(),
                orderProductResponseDtoList,
                order.getTotalPrice()
        );
    }
}
