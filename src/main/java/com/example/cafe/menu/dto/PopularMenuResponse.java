package com.example.cafe.menu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PopularMenuResponse {

    private final Long menuId;
    private final String name;
    private final Long price;
    private final Long orderCount;

    @Builder
    public PopularMenuResponse(Long menuId, String name, Long price, Long orderCount) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
    }
}
