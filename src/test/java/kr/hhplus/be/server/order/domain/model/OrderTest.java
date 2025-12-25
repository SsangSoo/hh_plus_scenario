package kr.hhplus.be.server.order.domain.model;

import kr.hhplus.be.server.order.domain.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrderTest() {
        Long memberId = 1L;

        Order order = Order.create(memberId);

        assertThat(order.getId()).isNull();
    }

    @Test
    @DisplayName("id는 입력받아야 한다.")
    void assignIdTest() {
        Long memberId = 1L;

        Order order = Order.create(memberId);

        assertThat(order.getId()).isNull();

        Long saveOrderId = 3L;
        order.assignId(saveOrderId);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getId()).isEqualTo(saveOrderId);
    }

    @Test
    @DisplayName("of 메서드 테스트")
    void ofTest() {
        Order order = Order.of(1L, 1L, LocalDateTime.of(2025, 12, 2, 10, 0, 0));

        assertThat(order).isNotNull();
        assertThat(order.getId()).isEqualTo(1L);
    }

}