package com.example.cafe.order.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Order(Long memberId, Long totalPrice, OrderStatus status) {
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = status != null ? status : OrderStatus.RECEIVED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    public void updateStatus(OrderStatus status) {
        if (!isTransitionAllowed(this.status, status)) {
            throw new com.example.cafe.global.error.CustomException(com.example.cafe.global.error.ErrorCode.ORDER_INVALID_STATUS);
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isTransitionAllowed(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.RECEIVED && next == OrderStatus.PREPARING) {
            return true;
        }
        if (current == OrderStatus.PREPARING && next == OrderStatus.READY_FOR_PICKUP) {
            return true;
        }
        if (current == OrderStatus.READY_FOR_PICKUP && next == OrderStatus.COMPLETED) {
            return true;
        }
        return false;
    }
}
