package com.example.cafe.menu.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menus")
    public ApiResponse<List<MenuResponse>> getMenus() {
        return ApiResponse.success(menuService.getAvailableMenus());
    }
}
