package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.exception.NotEnoughBalanceException;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletJpaRepository walletJpaRepository;
    private final WalletTransactionService walletTransactionService;

    @Transactional
    public void decrease(Wallet wallet, Long price, Long orderId){
        if(wallet.getBalance() < price) {
            throw new NotEnoughBalanceException("잔액이 부족합니다. (지갑 ID: " + wallet.getId() + ")");
        }

        wallet.decrease(price);
        walletTransactionService.createOrderTransaction(wallet, orderId, price);
    }

    @Transactional
    public void increase(Wallet wallet, Long price, Long orderProductId){
        wallet.increase(price);
        walletTransactionService.createRefundTransaction(wallet, orderProductId, price);
    }

    @Transactional
    public Wallet findByMemberIdWithLock(Long memberId){
        return walletJpaRepository.findByMemberIdWithLock(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지갑 입니다."));
    }

    @Transactional(readOnly = true)
    public WalletResponseDto findByMemberId(Long memberId){
        Wallet wallet = walletJpaRepository.findByMemberId(memberId);
        return new WalletResponseDto(
                wallet.getBalance()
        );
    }

    @Transactional
    public List<WalletTransactionResponseDto> getWalletTransactionsByMemberId(Long memberId){
        Wallet wallet = walletJpaRepository.findByMemberId(memberId);
        return walletTransactionService.findByWalletId(wallet.getId());
    }

    @Transactional
    public WalletChargeResponseDto charge(Long memberId, WalletChargeRequestDto walletChargeRequestDto){
        Wallet wallet = findByMemberIdWithLock(memberId);
        Long amount = walletChargeRequestDto.getAmount();
        wallet.increase(amount);
        walletTransactionService.createChargeTransaction(wallet, amount);
        return new WalletChargeResponseDto(
                wallet.getId(),
                wallet.getBalance()
        );
    }
}
