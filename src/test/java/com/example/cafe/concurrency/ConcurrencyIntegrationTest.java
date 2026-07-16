package com.example.cafe.concurrency;

import com.example.cafe.member.domain.Member;
import com.example.cafe.member.repository.MemberRepository;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.repository.MenuRepository;
import com.example.cafe.order.controller.OrderController;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.point.controller.PointController;
import com.example.cafe.point.dto.PointChargeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrencyIntegrationTest {

    @Autowired
    private PointController pointController;

    @Autowired
    private OrderController orderController;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private com.example.cafe.order.repository.OrderRepository orderRepository;

    @Autowired
    private com.example.cafe.point.repository.PointHistoryRepository pointHistoryRepository;

    private Long testMemberId;
    private Long testMenuId;

    @BeforeEach
    void setUp() {
        pointHistoryRepository.deleteAll();
        orderRepository.deleteAll();
        memberRepository.deleteAll();
        menuRepository.deleteAll();

        Member member = Member.builder()
                .username("concurrencyUser")
                .password("password123")
                .pointBalance(0L)
                .build();
        testMemberId = memberRepository.save(member).getId();

        Menu menu = Menu.builder()
                .name("Hot Americano")
                .price(3000L)
                .status(MenuStatus.AVAILABLE)
                .build();
        testMenuId = menuRepository.save(menu).getId();
    }

    @Test
    @DisplayName("동일 사용자에게 여러 충전 요청이 동시에 발생해도 재시도 메커니즘을 통해 최종 포인트 잔액이 완벽히 합산되어야 한다")
    void concurrencyPointChargeTest() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    PointChargeRequest request = PointChargeRequest.builder()
                            .memberId(testMemberId)
                            .amount(1000L)
                            .build();
                    pointController.chargePoint(request);
                } catch (Exception e) {
                    System.err.println("충전 에러: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        Member finalMember = memberRepository.findById(testMemberId).orElseThrow();
        assertThat(finalMember.getPointBalance()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("동일 사용자가 동시에 여러 주문을 생성해도 버전 충돌 시 재시도를 통해 모든 주문 결제가 최종적으로 성공해야 한다")
    void concurrencyOrderTest() throws InterruptedException {
        Member member = memberRepository.findById(testMemberId).orElseThrow();
        member.chargePoint(30000L);
        memberRepository.saveAndFlush(member);

        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    OrderCreateRequest.OrderItemRequest itemRequest = OrderCreateRequest.OrderItemRequest.builder()
                            .menuId(testMenuId)
                            .quantity(1)
                            .temperature(com.example.cafe.order.domain.Temperature.HOT)
                            .build();

                    OrderCreateRequest request = OrderCreateRequest.builder()
                            .memberId(testMemberId)
                            .items(List.of(itemRequest))
                            .build();

                    orderController.createOrder(request);
                } catch (Exception e) {
                    System.err.println("주문 에러: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        Member finalMember = memberRepository.findById(testMemberId).orElseThrow();
        assertThat(finalMember.getPointBalance()).isEqualTo(15000L);
    }
}
