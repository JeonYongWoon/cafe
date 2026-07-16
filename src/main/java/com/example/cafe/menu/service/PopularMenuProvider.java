package com.example.cafe.menu.service;

import java.time.LocalDateTime;
import java.util.List;

public interface PopularMenuProvider {

    List<PopularMenuResult> getPopularMenuResults(LocalDateTime startDate, int limit);
}
