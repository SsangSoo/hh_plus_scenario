package kr.hhplus.be.server.domain.point.service.request;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ChargePointTest {

    @Test
    @DisplayName("충전 포인트는 0 보다 커야 한다.")
    void chargePointIsPositiveTest() {
        // given : 충전 포인트 1(0 초과)
        Long memberId = 1L;
        Long requestChargePoint = 1L;

        // when : 충전 포인트 생성
        ChargePoint chargePoint = new ChargePoint(memberId, requestChargePoint);

        // then : 충전 포인트 양수 검증
        Assertions.assertThat(chargePoint.point()).isPositive();
    }

    @Test
    @DisplayName("충전하려는 포인트가 0 이라면 생성시 예외가 발생한다.")
    void chargePointIsPositiveTest2() {
        // given : 충전 포인트 0
        Long memberId = 1L;
        Long requestChargePoint = 0L;

        // when // then : 충전 포인트 생성시 예외 발생
        Assertions.assertThatThrownBy(() -> new ChargePoint(memberId, requestChargePoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(BusinessLogicMessage.CHARGE_POINT_NOT_POSITIVE.getMessage());
    }

    @Test
    @DisplayName("충전하려는 포인트가 음수라면 생성시 예외가 발생한다.")
    void chargePointIsPositiveTest3() {
        // given : 충전 포인트 -1(음수)
        Long memberId = 1L;
        Long requestChargePoint = -1L;

        // when // then : 충전 포인트 생성시 예외 발생
        Assertions.assertThatThrownBy(() -> new ChargePoint(memberId, requestChargePoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(BusinessLogicMessage.CHARGE_POINT_NOT_POSITIVE.getMessage());
    }


}