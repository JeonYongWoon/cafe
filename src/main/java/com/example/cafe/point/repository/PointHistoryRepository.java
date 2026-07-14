package com.example.cafe.point.repository;

import com.example.cafe.point.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
