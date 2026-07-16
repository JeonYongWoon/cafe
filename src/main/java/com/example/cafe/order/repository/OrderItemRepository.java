package com.example.cafe.order.repository;

import com.example.cafe.order.domain.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.menuId as menuId, SUM(oi.quantity) as orderCount " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.createdAt >= :startDate " +
           "GROUP BY oi.menuId " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<PopularMenuProjection> findPopularMenuIds(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
