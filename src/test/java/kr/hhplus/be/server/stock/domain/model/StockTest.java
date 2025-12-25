package kr.hhplus.be.server.stock.domain.model;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("productId로부터 Stock을 생성할 수 있다. 그리고 그렇게 만들어진 Stock의 수량은 0이다.")
    void createStockTest() {
        // given
        Long productId = 1L;

        // when
        Stock stock = Stock.create(productId);

        // then
        assertThat(stock).isNotNull();
        assertThat(stock.getProductId()).isEqualTo(productId);
        assertThat(stock.getQuantity()).isZero();
    }


    @Test
    @DisplayName("of 테스트")
    void ofTest() {
        Stock stock = Stock.of(1L, 1L, 30L);

        assertThat(stock).isNotNull();
        assertThat(stock.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("id를 추가할 수 있다.")
    void assignIdTest() {
        Stock stock = Stock.create(1L);

        assertThat(stock.getId()).isNull();

        stock.assignId(1L);

        assertThat(stock.getId()).isNotNull();
        assertThat(stock.getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("재고를 추가한다.")
    void addStockTest() {
        // given : productId 세팅 및 Stock 생성
        Stock stock = Stock.create(1L);

        // when : 재고 추가
        stock.addStock(4L);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4L);
    }

    @Test
    @DisplayName("재고를 차감한다.")
    void deductedStockTest() {
        // given
        Stock stock = Stock.create(1L);

        // 재고 추가
        stock.addStock(4L);

        // when : 재고 차감
        stock.deductedStock(2L);

        // then
        assertThat(stock.getQuantity()).isEqualTo(2L);
    }

    @Test
    @DisplayName("재고를 차감할 때 차감하려는 재고의 수가 더 크면 안 된다.")
    void deductedStockValidTest() {
        // given : productId 세팅 및 Stock 생성
        Stock stock = Stock.create(1L);

        // 재고 추가
        stock.addStock(4L);

        // when // then
        assertThatThrownBy(() -> stock.deductedStock(5L))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH.getMessage());
    }



}

