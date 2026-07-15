package com.example.cafe.menu.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

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
}
