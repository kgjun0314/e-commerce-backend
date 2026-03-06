package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletTransactionJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {
    private final WalletTransactionJpaRepository walletTransactionJpaRepository;

    @Transactional
    public void createOrderTransaction(Wallet wallet, Long orderId, Long amount) {
        WalletTransaction walletTransaction = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction);
        walletTransaction.setOrderId(orderId);
        walletTransaction.setType(WalletTransactionType.PAYMENT);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceAfter(wallet.getBalance());

        walletTransactionJpaRepository.save(walletTransaction);
    }

    @Transactional
    public void createChargeTransaction(Wallet wallet, Long amount) {
        WalletTransaction walletTransaction = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction);
        walletTransaction.setType(WalletTransactionType.CHARGE);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceAfter(wallet.getBalance());

        walletTransactionJpaRepository.save(walletTransaction);
    }

    @Transactional
    public void createRefundTransaction(Wallet wallet, Long orderProductId, Long amount) {
        WalletTransaction walletTransaction = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction);
        walletTransaction.setOrderProductId(orderProductId);
        walletTransaction.setType(WalletTransactionType.REFUND);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceAfter(wallet.getBalance());

        walletTransactionJpaRepository.save(walletTransaction);
    }

    @Transactional
    public List<WalletTransactionResponseDto> getWalletTransactions(Long walletId) {
        List<WalletTransaction> walletTransactions = walletTransactionJpaRepository.findByWalletId(walletId);
        List<WalletTransactionResponseDto> walletTransactionResponseDtos = new ArrayList<>();
        walletTransactions
                .forEach(walletTransaction -> {
                    walletTransactionResponseDtos.add(getWalletTransactionResponseDto(walletTransaction));
                });
        return walletTransactionResponseDtos;
    }

    public static WalletTransactionResponseDto getWalletTransactionResponseDto(WalletTransaction walletTransaction) {
        return new WalletTransactionResponseDto(
                walletTransaction.getId(),
                walletTransaction.getOrderId(),
                walletTransaction.getOrderProductId(),
                walletTransaction.getType(),
                walletTransaction.getAmount(),
                walletTransaction.getBalanceAfter(),
                walletTransaction.getCreatedDate()
        );
    }
}
