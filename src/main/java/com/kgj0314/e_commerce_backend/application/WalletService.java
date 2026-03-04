package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughBalanceException;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletJpaRepository walletJpaRepository;

    @Transactional
    public void decrease(Wallet wallet, Long price){
        if(wallet.getBalance() < price) {
            throw new NotEnoughBalanceException("잔액이 부족합니다. (지갑 ID: " + wallet.getId() + ")");
        }

        wallet.decrease(price);
    }

    @Transactional
    public void increase(Wallet wallet, Long price){
        wallet.increase(price);
    }

    @Transactional
    public Wallet findByMemberIdWithLock(Long memberId){
        return walletJpaRepository.findByMemberIdWithLock(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지갑 입니다."));
    }
}
