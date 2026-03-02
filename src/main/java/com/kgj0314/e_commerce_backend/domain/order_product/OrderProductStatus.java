package com.kgj0314.e_commerce_backend.domain.order_product;

import com.kgj0314.e_commerce_backend.domain.exception.CannotCancellableStatusException;
import lombok.Getter;


@Getter
public enum OrderProductStatus {
    CREATED {
        @Override
        public void checkCancellable() {}
    },
    SHIPPING,
    COMPLETED,
    CANCELED;

    public void checkCancellable() {
        throw new CannotCancellableStatusException("주문이 이미 취소되었거나, 주문을 취소할 수 없는 상태입니다.");
    }
}
