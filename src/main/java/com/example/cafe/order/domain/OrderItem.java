package com.example.cafe.order.domain;

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

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature", nullable = false)
    private Temperature temperature;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Long price;

    @Builder
    public OrderItem(Order order, Long menuId, Temperature temperature, Integer quantity, Long price) {
        this.order = order;
        this.menuId = menuId;
        this.temperature = temperature;
        this.quantity = quantity;
        this.price = price;
    }
}
