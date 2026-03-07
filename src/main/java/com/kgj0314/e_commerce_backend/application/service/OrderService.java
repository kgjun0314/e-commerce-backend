package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderPageDto;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    OrderedProduct orderedProduct = new OrderedProduct(product, orderCommand.getQuantity());

                    order.addOrderedProduct(orderedProduct);
                });
        Member member = wallet.getMember();
        member.addOrder(order);
        Order savedOrder = orderJpaRepository.save(order);
        walletService.decreaseBalance(wallet, savedOrder.getTotalPrice(), savedOrder.getId());
        return new OrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long id) {
        Order order = orderJpaRepository.findByIdFetchJoin(id);
        return new OrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderPageDto getOrders(Long memberId, Pageable pageable) {
        Page<Long> orderIdPage = orderJpaRepository.findOrderIdByMemberId(memberId, pageable);

        if(orderIdPage.isEmpty()) {
            return new OrderPageDto(List.of(), 0, 0, pageable.getPageNumber(), pageable.getPageSize());
        }
        List<Order> orders = orderJpaRepository.findByIdListFetchJoin(orderIdPage.getContent());

        List<OrderResponseDto> orderResponseDtoList = orders.stream().map(OrderResponseDto::new).toList();
        return new OrderPageDto(
                orderResponseDtoList,
                orderIdPage.getTotalElements(),
                orderIdPage.getTotalPages(),
                orderIdPage.getNumber(),
                orderIdPage.getSize()
        );
    }
}
