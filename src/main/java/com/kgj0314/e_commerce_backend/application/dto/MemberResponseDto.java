package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;

    public MemberResponseDto (Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }
}
