package com.example.cafe.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    MEMBER_DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "MEMBER_DUPLICATE_USERNAME", "이미 사용 중인 아이디입니다."),
    MEMBER_INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "MEMBER_INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_NOT_FOUND", "해당 회원을 찾을 수 없습니다."),
    POINT_INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "POINT_INVALID_CHARGE_AMOUNT", "충전 금액은 최소 1,000원 이상이어야 합니다."),
    MENU_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "MENU_NOT_AVAILABLE", "존재하지 않는 메뉴이거나 판매 불가능한 상태입니다."),
    POINT_INSUFFICIENT(HttpStatus.BAD_REQUEST, "POINT_INSUFFICIENT", "보유 포인트 잔액이 부족하여 결제에 실패했습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "해당 주문을 찾을 수 없습니다."),
    ORDER_UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "ORDER_UNAUTHORIZED_ACCESS", "본인의 주문 내역만 조회할 수 있습니다."),
    SYSTEM_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "SYSTEM_INVALID_INPUT_VALUE", "입력값이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
