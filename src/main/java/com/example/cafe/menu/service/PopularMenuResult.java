package com.example.cafe.menu.service;

import lombok.Getter;

@Getter
public class PopularMenuResult {

    private final Long menuId;
    private final Long orderCount;

    public PopularMenuResult(Long menuId, Long orderCount) {
        this.menuId = menuId;
        this.orderCount = orderCount;
    }
}
