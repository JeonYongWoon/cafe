package com.example.cafe.menu.dto;

import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuResponse {

    private Long menuId;
    private String name;
    private Long price;
    private MenuStatus status;
    private String imageUrl;

    @Builder
    private MenuResponse(Long menuId, String name, Long price, MenuStatus status, String imageUrl) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .status(menu.getStatus())
                .imageUrl(menu.getImageUrl())
                .build();
    }
}
