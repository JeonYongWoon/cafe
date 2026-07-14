package com.example.cafe.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "DUPLICATE_USERNAME", "이미 사용 중인 아이디입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_NOT_FOUND", "해당 회원을 찾을 수 없습니다."),
    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_CHARGE_AMOUNT", "충전 금액은 최소 1,000원 이상이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
