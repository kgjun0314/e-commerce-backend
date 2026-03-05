package com.kgj0314.e_commerce_backend.application;

import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
