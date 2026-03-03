package com.kgj0314.e_commerce_backend.presentation.dto;

import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
    private String email;
    private String username;
    private String password;
}
