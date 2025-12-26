package kr.hhplus.be.server.orderproduct.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @Test
    @DisplayName("주문상품 생성 테스트")
    void createOrderProductTest() {
        OrderProduct orderProduct = OrderProduct.create(1L, 1L, 3L);

        assertThat(orderProduct.getId()).isNull();
        assertThat(orderProduct.getQuantity()).isEqualTo(3L);
    }

    @Test
    @DisplayName("of 으로 테스트")
    void ofTest() {
        OrderProduct orderProduct = OrderProduct.of(1L, 1L, 1L, 3L );

        assertThat(orderProduct.getId()).isNotNull();
        assertThat(orderProduct.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("id를 메서드로 추가할 수 있다.")
    void assignIdTest() {
        OrderProduct orderProduct = OrderProduct.create(1L, 1L, 3L);

        assertThat(orderProduct.getId()).isNull();

        orderProduct.assignId(1L);

        assertThat(orderProduct.getId()).isNotNull();
        assertThat(orderProduct.getId()).isEqualTo(1L);
    }

}