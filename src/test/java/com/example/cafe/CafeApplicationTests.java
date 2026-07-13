package com.example.cafe;

import com.example.cafe.menu.domain.Menu;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CafeApplicationTests {

    @Autowired
    private EntityManager em;

    @Test
    void contextLoads() {
    }

    @Test
    void testMenusDummyDataLoading() {
        List<Menu> menus = em.createQuery("select m from Menu m", Menu.class).getResultList();

        assertThat(menus).hasSize(8);
        assertThat(menus)
                .extracting(Menu::getName)
                .containsExactlyInAnyOrder(
                        "아메리카노",
                        "카페라떼",
                        "돌체라떼",
                        "카라멜마키아토",
                        "바닐라라떼",
                        "카푸치노",
                        "에스프레소",
                        "자몽에이드"
                );
    }
}
