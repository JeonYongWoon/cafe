package com.example.cafe.order.domain;

import com.example.cafe.menu.domain.Menu;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature", nullable = false)
    private Temperature temperature;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Long price;

    @Builder
    public OrderItem(Menu menu, Temperature temperature, Integer quantity, Long price) {
        this.menu = menu;
        this.temperature = temperature;
        this.quantity = quantity;
        this.price = price;
    }

    void setOrder(Order order) {
        this.order = order;
    }
}
