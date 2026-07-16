package com.example.cafe.order.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.order.dto.AdminOrderResponse;
import com.example.cafe.order.facade.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderFacade orderFacade;

    @GetMapping
    public ApiResponse<List<AdminOrderResponse>> getAllOrders() {
        return ApiResponse.success(orderFacade.getAllOrdersForAdmin());
    }
}
