package com.example.cafe.menu.repository;

import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByStatusIn(List<MenuStatus> statuses);
}
