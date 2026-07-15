package com.example.cafe.menu.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MenuStatus status;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Menu(String name, Long price, MenuStatus status, String imageUrl) {
        this.name = name;
        this.price = price;
        this.status = status != null ? status : MenuStatus.AVAILABLE;
        this.imageUrl = imageUrl;
    }

    public void validateAvailable() {
        if (this.status == MenuStatus.SOLD_OUT || this.status == MenuStatus.DISCONTINUED) {
            throw new com.example.cafe.global.error.CustomException(com.example.cafe.global.error.ErrorCode.MENU_NOT_AVAILABLE);
        }
    }
}
