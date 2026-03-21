package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.stock.Stock;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockJpaRepository stockJpaRepository;

    @Transactional
    public void decreaseQuantity(Stock stock, Long quantity) {
        if(stock == null) {
            throw new EntityNotFoundException("존재하지 않는 상품/재고 정보입니다.");
        }
        stock.decreaseQuantity(quantity);
    }

    @Transactional
    public void increaseQuantity(Stock stock, Long quantity) {
        stock.increaseQuantity(quantity);
    }

    @Transactional
    public Stock getStockWithLock(Long productId) {
        return stockJpaRepository.findByProductIdWithLock(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 재고 정보입니다."));
    }
}
