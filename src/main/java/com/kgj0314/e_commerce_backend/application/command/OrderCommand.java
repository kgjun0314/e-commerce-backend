package com.kgj0314.e_commerce_backend.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderCommand {
    private Long productId;
    private Long quantity;
}
