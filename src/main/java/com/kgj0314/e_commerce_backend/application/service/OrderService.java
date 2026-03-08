package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderPageDto;
import com.kgj0314.e_commerce_backend.domain.order.Order;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.OrderResponseDto;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final StockJpaRepository stockJpaRepository;
    private final StockService stockService;
    private final WalletService walletService;

    @Transactional
    public OrderResponseDto createOrder(Long memberId, List<OrderCommand> orderCommandList) {
        Map<Long, Long> quantityByProduct = orderCommandList.stream()
                .collect(Collectors.groupingBy(
                        OrderCommand::getProductId,
                        Collectors.summingLong(OrderCommand::getQuantity)
                ));

        List<Long> sortedProductIds = quantityByProduct.keySet()
                .stream()
//                .sorted()
                .toList();

        List<Stock> stocks = stockJpaRepository.findByProductIdListWithLock(sortedProductIds);

        Map<Long, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(
                        s -> s.getProduct().getId(),
                        Function.identity()
                ));

        Order order = new Order();
        Wallet wallet = walletService.getWalletWithLock(memberId);
        sortedProductIds
                .forEach(productId -> {
                    Long quantity = quantityByProduct.get(productId);
                    Stock stock = stockMap.get(productId);

                    stockService.decreaseQuantity(stock, quantity);

                    Product product = stock.getProduct();
                    OrderedProduct orderedProduct = new OrderedProduct(product, quantity);

                    order.addOrderedProduct(orderedProduct);
                });
        order.setMember(wallet.getMember());
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
