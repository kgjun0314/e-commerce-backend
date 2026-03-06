package com.kgj0314.e_commerce_backend.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WalletChargeCommand {
    private Long amount;
}
