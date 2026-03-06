package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.domain.exception.EntityNotFoundException;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletJpaRepository;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeRequestDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletChargeResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletResponseDto;
import com.kgj0314.e_commerce_backend.presentation.dto.WalletTransactionResponseDto;
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
    public void increaseBalance(Wallet wallet, Long price, Long orderProductId){
        wallet.increaseBalance(price);
        walletTransactionService.createRefundTransaction(wallet, orderProductId, price);
    }

    @Transactional
    public Wallet getWalletWithLock(Long memberId){
        return walletJpaRepository.findByMemberIdWithLock(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지갑 입니다."));
    }

    @Transactional(readOnly = true)
    public WalletResponseDto getWallet(Long memberId){
        Wallet wallet = walletJpaRepository.findByMemberId(memberId);
        return new WalletResponseDto(
                wallet.getBalance()
        );
    }

    @Transactional
    public List<WalletTransactionResponseDto> getWalletTransactions(Long memberId){
        Wallet wallet = walletJpaRepository.findByMemberIdFetchJoin(memberId);
        List<WalletTransaction> walletTransactions = wallet.getTransactions();
        List<WalletTransactionResponseDto> walletTransactionResponseDtos = new ArrayList<>();
        walletTransactions
                .forEach(walletTransaction -> {
                    walletTransactionResponseDtos.add(WalletTransactionService.getWalletTransactionResponseDto(walletTransaction));
                });
        return walletTransactionResponseDtos;
    }

    @Transactional
    public WalletChargeResponseDto chargeWallet(Long memberId, WalletChargeRequestDto walletChargeRequestDto){
        Wallet wallet = getWalletWithLock(memberId);
        Long amount = walletChargeRequestDto.getAmount();
        wallet.increaseBalance(amount);
        walletTransactionService.createChargeTransaction(wallet, amount);
        return new WalletChargeResponseDto(
                wallet.getId(),
                wallet.getBalance()
        );
    }
}
