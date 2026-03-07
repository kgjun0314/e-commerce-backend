package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.OrderedProductStatusCommand;
import com.kgj0314.e_commerce_backend.application.dto.OrderedProductResponseDto;
import com.kgj0314.e_commerce_backend.application.query.OrderedProductStatusQuery;
import com.kgj0314.e_commerce_backend.domain.exception.CannotCancellableStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.CannotChangeStatusException;
import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.exception.MemberIdMismatchException;
import com.kgj0314.e_commerce_backend.domain.member.Member;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProduct;
import com.kgj0314.e_commerce_backend.domain.ordered_product.OrderedProductStatus;
import com.kgj0314.e_commerce_backend.domain.product.Product;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.OrderedProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderedProductService {
    private final OrderedProductJpaRepository orderedProductJpaRepository;
    private final StockService stockService;
    private final WalletService walletService;

    @Transactional(readOnly = true)
    public OrderedProductResponseDto getOrderedProduct(Long id) {
        OrderedProduct orderedProduct = orderedProductJpaRepository.findByIdFetchJoin(id);
        Product product = orderedProduct.getProduct();
        return new OrderedProductResponseDto(orderedProduct, product);
    }

    @Transactional
    public OrderedProductResponseDto changeStatus(Long id, OrderedProductStatusCommand orderedProductStatusCommand) {
        OrderedProductStatus nextStatus = orderedProductStatusCommand.getStatus();
        if(nextStatus == OrderedProductStatus.CANCELED) {
            throw new CannotCancellableStatusException("주문 취소 API를 호출하세요.");
        }
        OrderedProduct orderedProduct = orderedProductJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        OrderedProductStatus currentStatus = orderedProduct.getStatus();

        if(nextStatus == currentStatus) {
            throw new CannotChangeStatusException("변경 전과 변경 후가 동일합니다.");
        }
        else if(currentStatus == OrderedProductStatus.CANCELED) {
            throw new CannotChangeStatusException("이미 취소된 주문입니다.");
        }

        orderedProduct.setStatus(nextStatus);
        return new OrderedProductResponseDto(orderedProduct, orderedProduct.getProduct());
    }

    @Transactional
    public OrderedProductResponseDto cancelOrderedProduct(Member member, Long id) {
        OrderedProduct orderedProduct = orderedProductJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문 정보입니다."));
        if(!member.getId().equals(orderedProduct.getOrder().getMember().getId())) {
            throw new MemberIdMismatchException("리소스의 소유자와 로그인 사용자가 서로 다릅니다.");
        }
        OrderedProductStatus status = orderedProduct.getStatus();
        status.checkCancellable();
        Product product = orderedProduct.getProduct();
        Stock stock = stockService.getStockWithLock(product.getId());
        stockService.increaseStock(stock, orderedProduct.getQuantity());
        Wallet wallet = walletService.getWalletWithLock(member.getId());
        walletService.increaseBalance(wallet, orderedProduct.getTotalPrice(), orderedProduct.getId());
        orderedProduct.setStatus(OrderedProductStatus.CANCELED);
        return new OrderedProductResponseDto(orderedProduct, product);
    }

    @Transactional
    public List<OrderedProductResponseDto> getOrderedProducts(OrderedProductStatusQuery orderedProductStatusQuery) {
        OrderedProductStatus status = orderedProductStatusQuery.getStatus();
        List<OrderedProduct> orderedProductList = orderedProductJpaRepository.findByStatusFetchJoin(status);
        List<OrderedProductResponseDto> orderedProductResponseDtos = new ArrayList<>();
        orderedProductList
                .forEach(orderedProduct -> {
                    orderedProductResponseDtos.add(new OrderedProductResponseDto(orderedProduct, orderedProduct.getProduct()));
                });
        return orderedProductResponseDtos;
    }
}
