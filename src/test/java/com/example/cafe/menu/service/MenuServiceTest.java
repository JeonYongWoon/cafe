package com.example.cafe.menu.service;

import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    void getAvailableMenusSuccess() {
        Menu americano = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .imageUrl("/images/americano.png")
                .build();
        ReflectionTestUtils.setField(americano, "id", 1L);

        Menu latte = Menu.builder()
                .name("카페라떼")
                .price(5000L)
                .status(MenuStatus.SOLD_OUT)
                .imageUrl("/images/cafelatte.png")
                .build();
        ReflectionTestUtils.setField(latte, "id", 2L);

        when(menuRepository.findAllByStatusIn(anyList())).thenReturn(Arrays.asList(americano, latte));

        List<MenuResponse> result = menuService.getAvailableMenus();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(0).getStatus()).isEqualTo(MenuStatus.AVAILABLE);
        assertThat(result.get(1).getMenuId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("카페라떼");
        assertThat(result.get(1).getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
    }
}
