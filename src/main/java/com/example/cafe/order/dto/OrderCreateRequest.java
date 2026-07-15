package com.example.cafe.order.dto;

import com.example.cafe.order.domain.Temperature;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreateRequest {

    @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:회원 ID는 필수입니다.")
    private Long memberId;

    @NotEmpty(message = "SYSTEM_INVALID_INPUT_VALUE:주문 상품은 최소 1개 이상이어야 합니다.")
    @Valid
    private List<OrderItemRequest> items;

    @Builder
    public OrderCreateRequest(Long memberId, List<OrderItemRequest> items) {
        this.memberId = memberId;
        this.items = items;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderItemRequest {
        
        @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:메뉴 ID는 필수입니다.")
        private Long menuId;

        @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:온도는 필수입니다.")
        private Temperature temperature;

        @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:수량은 필수입니다.")
        @Min(value = 1, message = "SYSTEM_INVALID_INPUT_VALUE:수량은 최소 1개 이상이어야 합니다.")
        private Integer quantity;

        @Builder
        public OrderItemRequest(Long menuId, Temperature temperature, Integer quantity) {
            this.menuId = menuId;
            this.temperature = temperature;
            this.quantity = quantity;
        }
    }
}
