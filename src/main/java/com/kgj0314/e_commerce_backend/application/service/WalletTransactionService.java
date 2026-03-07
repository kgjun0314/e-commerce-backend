package com.kgj0314.e_commerce_backend.application.service;

import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionPageDto;
import com.kgj0314.e_commerce_backend.domain.wallet.Wallet;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransaction;
import com.kgj0314.e_commerce_backend.domain.wallet.WalletTransactionType;
import com.kgj0314.e_commerce_backend.infrastructure.persistence.WalletTransactionJpaRepository;
import com.kgj0314.e_commerce_backend.application.dto.WalletTransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void createRefundTransaction(Wallet wallet, Long orderedProductId, Long amount) {
        WalletTransaction walletTransaction = new WalletTransaction();
        wallet.addWalletTransaction(walletTransaction);
        walletTransaction.setOrderedProductId(orderedProductId);
        walletTransaction.setType(WalletTransactionType.REFUND);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceAfter(wallet.getBalance());

        walletTransactionJpaRepository.save(walletTransaction);
    }

    @Transactional(readOnly = true)
    public WalletTransactionPageDto getWalletTransactions(Long memberId, Pageable pageable) {
        Page<Long> walletTransactionIdPage = walletTransactionJpaRepository.findWalletTransactionIdByWalletId(memberId, pageable);

        if(walletTransactionIdPage.isEmpty()) {
            return new WalletTransactionPageDto(List.of(), 0, 0, pageable.getPageNumber(), pageable.getPageSize());
        }
        List<WalletTransaction> walletTransactionList = walletTransactionJpaRepository.findByIdListFetchJoin(walletTransactionIdPage.getContent());

        List<WalletTransactionResponseDto> walletTransactionResponseDtoList = walletTransactionList.stream()
                .map(WalletTransactionResponseDto::new)
                .toList();
        return new WalletTransactionPageDto(
                walletTransactionResponseDtoList,
                walletTransactionIdPage.getTotalElements(),
                walletTransactionIdPage.getTotalPages(),
                walletTransactionIdPage.getNumber(),
                walletTransactionIdPage.getSize()
        );
    }
}
