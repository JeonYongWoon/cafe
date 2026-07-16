package com.example.cafe.menu.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.dto.PopularMenuResponse;
import com.example.cafe.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final PopularMenuProvider popularMenuProvider;

    public List<MenuResponse> getAvailableMenus() {
        List<MenuStatus> targetStatuses = Arrays.asList(MenuStatus.AVAILABLE, MenuStatus.SOLD_OUT);
        return menuRepository.findAllByStatusIn(targetStatuses).stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_AVAILABLE));
    }

    public List<Menu> getMenus(List<Long> menuIds) {
        return menuRepository.findAllById(menuIds);
    }

    public List<PopularMenuResponse> getPopularMenus(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<PopularMenuResult> popularMenuResults = popularMenuProvider.getPopularMenuResults(startDate, 3);

        List<Long> menuIds = popularMenuResults.stream()
                .map(PopularMenuResult::getMenuId)
                .collect(Collectors.toList());

        List<Menu> menus = getMenus(menuIds);

        return popularMenuResults.stream()
                .map(result -> {
                    Menu menu = menus.stream()
                            .filter(m -> m.getId().equals(result.getMenuId()))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_AVAILABLE));
                    return PopularMenuResponse.builder()
                            .menuId(menu.getId())
                            .name(menu.getName())
                            .price(menu.getPrice())
                            .orderCount(result.getOrderCount())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
