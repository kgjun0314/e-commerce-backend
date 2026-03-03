package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private String email;
    private String username;
}
