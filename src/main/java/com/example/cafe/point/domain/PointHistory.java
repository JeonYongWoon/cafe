package com.example.cafe.point.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public PointHistory(Long memberId, Long orderId, Long amount, PointType type) {
        this.memberId = memberId;
        this.orderId = orderId;
        this.amount = amount;
        this.type = type;
    }
}
