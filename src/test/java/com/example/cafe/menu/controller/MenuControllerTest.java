package com.example.cafe.menu.controller;

import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.dto.MenuResponse;
import com.example.cafe.menu.dto.PopularMenuResponse;
import com.example.cafe.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
    }

    @Test
    void getMenusSuccess() throws Exception {
        MenuResponse response1 = MenuResponse.builder()
                .menuId(1L)
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .imageUrl("/images/americano.png")
                .build();
        MenuResponse response2 = MenuResponse.builder()
                .menuId(2L)
                .name("카페라떼")
                .price(5000L)
                .status(MenuStatus.SOLD_OUT)
                .imageUrl("/images/cafelatte.png")
                .build();

        when(menuService.getAvailableMenus()).thenReturn(Arrays.asList(response1, response2));

        mockMvc.perform(get("/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].menuId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("아메리카노"))
                .andExpect(jsonPath("$.data[0].price").value(4500))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data[0].imageUrl").value("/images/americano.png"))
                .andExpect(jsonPath("$.data[1].menuId").value(2))
                .andExpect(jsonPath("$.data[1].name").value("카페라떼"))
                .andExpect(jsonPath("$.data[1].price").value(5000))
                .andExpect(jsonPath("$.data[1].status").value("SOLD_OUT"))
                .andExpect(jsonPath("$.data[1].imageUrl").value("/images/cafelatte.png"))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void getPopularMenusSuccess() throws Exception {
        PopularMenuResponse response1 = PopularMenuResponse.builder()
                .menuId(1L)
                .name("아메리카노")
                .price(4500L)
                .orderCount(420L)
                .build();
        PopularMenuResponse response2 = PopularMenuResponse.builder()
                .menuId(3L)
                .name("돌체라떼")
                .price(5500L)
                .orderCount(310L)
                .build();

        when(menuService.getPopularMenus(7)).thenReturn(Arrays.asList(response1, response2));

        mockMvc.perform(get("/menus/popular").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].menuId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("아메리카노"))
                .andExpect(jsonPath("$.data[0].price").value(4500))
                .andExpect(jsonPath("$.data[0].orderCount").value(420))
                .andExpect(jsonPath("$.data[1].menuId").value(3))
                .andExpect(jsonPath("$.data[1].name").value("돌체라떼"))
                .andExpect(jsonPath("$.data[1].price").value(5500))
                .andExpect(jsonPath("$.data[1].orderCount").value(310))
                .andExpect(jsonPath("$.error").isEmpty());
    }
}
