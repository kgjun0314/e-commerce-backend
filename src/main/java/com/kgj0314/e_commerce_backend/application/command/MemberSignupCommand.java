package com.kgj0314.e_commerce_backend.application.command;

import com.kgj0314.e_commerce_backend.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberSignupCommand {
    private String email;
    private String username;
    private String password;
    private Role role;
}
