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
    public OrderItem(Long menuId, Temperature temperature, Integer quantity, Long price) {
        this.menuId = menuId;
        this.temperature = temperature;
        this.quantity = quantity;
        this.price = price;
    }

    // package-private: Order.addOrderItem()이 유일한 진입점. 동일 패키지 외부(타 컨텍스트)에서는 호출 불가
    void setOrder(Order order) {
        this.order = order;
    }
}
