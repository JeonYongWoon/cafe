package com.example.cafe.menu.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.dto.PopularMenuResponse;
import com.example.cafe.menu.service.MenuService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menus")
    public ApiResponse<List<MenuResponse>> getMenus() {
        return ApiResponse.success(menuService.getAvailableMenus());
    }

    @GetMapping("/menus/popular")
    public ApiResponse<List<PopularMenuResponse>> getPopularMenus(
            @RequestParam(name = "days", defaultValue = "7") @Positive(message = "조회 기간은 양수여야 합니다.") int days) {
        return ApiResponse.success(menuService.getPopularMenus(days));
    }
}
