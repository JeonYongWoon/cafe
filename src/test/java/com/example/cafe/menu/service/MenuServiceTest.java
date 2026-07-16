package com.example.cafe.menu.service;

import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.dto.PopularMenuResponse;
import com.example.cafe.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PopularMenuProvider popularMenuProvider;

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

    @Test
    void getPopularMenusSuccess() {
        PopularMenuResult result1 = new PopularMenuResult(3L, 10L);
        PopularMenuResult result2 = new PopularMenuResult(1L, 5L);

        when(popularMenuProvider.getPopularMenuResults(any(LocalDateTime.class), eq(3)))
                .thenReturn(Arrays.asList(result1, result2));

        Menu menu3 = Menu.builder()
                .name("돌체라떼")
                .price(5500L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu3, "id", 3L);

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu1, "id", 1L);

        when(menuRepository.findAllById(Arrays.asList(3L, 1L)))
                .thenReturn(Arrays.asList(menu3, menu1));

        List<PopularMenuResponse> results = menuService.getPopularMenus(7);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getMenuId()).isEqualTo(3L);
        assertThat(results.get(0).getName()).isEqualTo("돌체라떼");
        assertThat(results.get(0).getOrderCount()).isEqualTo(10L);

        assertThat(results.get(1).getMenuId()).isEqualTo(1L);
        assertThat(results.get(1).getName()).isEqualTo("아메리카노");
        assertThat(results.get(1).getOrderCount()).isEqualTo(5L);
    }
}
