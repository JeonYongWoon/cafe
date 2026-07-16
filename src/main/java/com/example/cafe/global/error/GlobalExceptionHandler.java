package com.example.cafe.global.error;

import com.example.cafe.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<?> response = ApiResponse.fail(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        org.springframework.validation.FieldError fieldError = e.getBindingResult().getFieldError();
        String code = "SYSTEM_INVALID_INPUT_VALUE";
        String message = "입력값이 올바르지 않습니다.";
        if (fieldError != null) {
            String defaultMessage = fieldError.getDefaultMessage();
            if (defaultMessage != null && defaultMessage.contains(":")) {
                String[] split = defaultMessage.split(":", 2);
                code = split[0];
                message = split[1];
            } else if (defaultMessage != null) {
                message = defaultMessage;
            }
        }
        ApiResponse<?> response = ApiResponse.fail(code, message);
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException e) {
        String code = "SYSTEM_INVALID_INPUT_VALUE";
        String message = "입력값이 올바르지 않습니다.";
        if (e.getCause() != null && e.getCause().getClass().getSimpleName().equals("InvalidFormatException")) {
            try {
                java.lang.reflect.Method getTargetTypeMethod = e.getCause().getClass().getMethod("getTargetType");
                Class<?> targetType = (Class<?>) getTargetTypeMethod.invoke(e.getCause());
                if (targetType != null && targetType.isEnum()) {
                    code = "ORDER_INVALID_STATUS";
                    message = "올바르지 않은 주문 상태입니다.";
                }
            } catch (Exception ex) {
            }
        }
        ApiResponse<?> response = ApiResponse.fail(code, message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        ApiResponse<?> response = ApiResponse.fail("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return ResponseEntity
                .status(500)
                .body(response);
    }
}
