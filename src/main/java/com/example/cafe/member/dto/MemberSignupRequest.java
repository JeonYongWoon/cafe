package com.example.cafe.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {
    private String username;
    private String password;

    @Builder
    public MemberSignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
