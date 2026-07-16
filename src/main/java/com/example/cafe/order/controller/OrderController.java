package com.example.cafe.order.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.order.dto.OrderDetailResponse;
import com.example.cafe.order.dto.OrderResponse;
import com.example.cafe.order.dto.OrderStatusUpdateRequest;
import com.example.cafe.order.dto.OrderStatusUpdateResponse;
import com.example.cafe.order.facade.OrderFacade;
import com.example.cafe.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderFacade.createOrder(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(
            @PathVariable("orderId") @Positive Long orderId,
            @RequestHeader(value = "X-Member-Id", required = false) Long headerMemberId,
            @RequestParam(value = "memberId", required = false) Long paramMemberId) {
        Long loginMemberId = headerMemberId != null ? headerMemberId : paramMemberId;
        OrderDetailResponse response = orderFacade.getOrderDetail(orderId, loginMemberId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable("orderId") @Positive Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderStatusUpdateResponse response = orderService.updateOrderStatus(orderId, request.getStatus());
        return ApiResponse.success(response);
    }
}
