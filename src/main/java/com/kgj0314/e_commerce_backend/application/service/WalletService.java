package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.command.WalletChargeCommand;
import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletResponseDto;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletJpaRepository walletJpaRepository;
    private final WalletTransactionService walletTransactionService;

    @Transactional
    public void decreaseBalance(Wallet wallet, Long price, Long orderId){
        wallet.decreaseBalance(price);
        walletTransactionService.createOrderTransaction(wallet, orderId, price);
    }

    @Transactional
    public void increaseBalance(Wallet wallet, Long price, Long orderedProductId){
        wallet.increaseBalance(price);
        walletTransactionService.createRefundTransaction(wallet, orderedProductId, price);
    }

    @Transactional
    public Wallet getWalletWithLock(Long memberId){
        return walletJpaRepository.findByMemberIdWithLock(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지갑 입니다."));
    }

    @Transactional(readOnly = true)
    public WalletResponseDto getWallet(Long memberId){
        Wallet wallet = walletJpaRepository.findByMemberId(memberId);
        return new WalletResponseDto(wallet);
    }

    @Transactional
    public WalletChargeResponseDto chargeWallet(Long memberId, WalletChargeCommand walletChargeCommand){
        Wallet wallet = getWalletWithLock(memberId);
        Long amount = walletChargeCommand.getAmount();
        wallet.increaseBalance(amount);
        walletTransactionService.createChargeTransaction(wallet, amount);
        return new WalletChargeResponseDto(wallet);
    }
}
