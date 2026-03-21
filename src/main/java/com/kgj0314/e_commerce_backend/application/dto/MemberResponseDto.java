package com.kgj0314.e_commerce_backend.application.dto;

import com.kgj0314.e_commerce_backend.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    @Schema(description = "이메일", example = "mail@email.com")
    private String email;
    @Schema(description = "사용자명", example = "username")
    private String username;

    public MemberResponseDto (Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }
}
