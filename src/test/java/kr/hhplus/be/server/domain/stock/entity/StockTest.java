package kr.hhplus.be.server.domain.stock.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("productId로부터 Stock을 생성할 수 있다. 그리고 그렇게 만들어진 Stock의 수량은 0이다.")
    void createStockTest() {
        // given : productId 세팅
        Long productId = 1L;

        // when : of 메서드로 Stock 생성
        Stock stock = Stock.register(productId);

        // then
        assertThat(stock).isNotNull();
        assertThat(stock.getProductId()).isEqualTo(productId);
        assertThat(stock.getQuantity()).isZero();
        assertThat(stock.getCreatedDate()).isEqualTo(stock.getModifiedDate());
        assertThat(stock.getDeleted()).isFalse();
    }

    @Test
    @DisplayName("재고를 삭제할 수 있다.")
    void stockDeleteTest() {
        // given : productId 세팅 및 Stock 생성
        Long productId = 1L;

        Stock stock = Stock.register(productId);

        // when : Stock 삭제
        stock.delete();

        // then
        assertThat(stock.getDeleted()).isTrue();

    }

}

