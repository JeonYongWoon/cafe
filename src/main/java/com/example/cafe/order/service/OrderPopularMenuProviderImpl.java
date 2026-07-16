package com.example.cafe.order.service;

import com.example.cafe.menu.service.PopularMenuProvider;
import com.example.cafe.menu.service.PopularMenuResult;
import com.example.cafe.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPopularMenuProviderImpl implements PopularMenuProvider {

    private final OrderItemRepository orderItemRepository;

    @Override
    public List<PopularMenuResult> getPopularMenuResults(LocalDateTime startDate, int limit) {
        return orderItemRepository.findPopularMenuIds(startDate, PageRequest.of(0, limit)).stream()
                .map(projection -> new PopularMenuResult(projection.getMenuId(), projection.getOrderCount()))
                .collect(Collectors.toList());
    }
}
