package com.example.cafe.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionCreateRequest {

    private String username;
    private String password;

    public SessionCreateRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
