package kr.hhplus.be.server.point.domain.model;

import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class PointTest {

    @Test
    @DisplayName("회원의 Id로 Point는 생성된다. 그리고 생성된 Point는 0원이다.")
    void createPointWithMemberIdTest() {
        // given // when
        Point point = Point.create(1L);

        // then : 포인트 생성 검증
        assertThat(point).isNotNull();
        assertThat(point.getId()).isNull();
        assertThat(point.getPoint()).isZero();
    }

    @Test
    @DisplayName("of 메서드 테스트")
    void ofPointTest() {
        // given // when
        Point point = Point.of(1L, 1L, 3000L);

        // then : 포인트 생성 검증
        assertThat(point).isNotNull();
        assertThat(point.getId()).isNotNull();
        assertThat(point.getPoint()).isEqualTo(3000L);
    }

    @Test
    @DisplayName("id를 주입할 수 있다.")
    void assignIdTest() {
        Point point = Point.create(1L);

        assertThat(point.getId()).isNull();

        point.assignId(1L);


        assertThat(point.getId()).isNotNull();
        assertThat(point.getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePointTest() {
        // given : 포인트 충전을 위한 값 설정
        Point point = Point.create(1L);

        ChargePoint chargePoint = new ChargePoint(1L, 100L);

        // when : 포인트 충전
        point.charge(chargePoint);

        // then : 충전된 포인트 확인
        assertThat(point.getPoint()).isEqualTo(100L);
    }


    @Test
    @DisplayName("0이하의 값으로 포인트를 충전할 경우 예외가 발생한다.")
    void cannotChargePointZeroTest() {
        // given : 포인트 충전을 위한 값 설정
        Point point = Point.create(1L);

        // when // then : 0이하의 값으로 충전할 경우 예외 발생
        assertThatThrownBy(() -> point.charge(new ChargePoint(1L, 0L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(BusinessLogicMessage.CHARGE_POINT_NOT_POSITIVE.getMessage());
    }


    @Test
    @DisplayName("음수로 포인트를 충전할 경우 예외가 발생한다.")
    void cannotChargePointNegativeTest() {
        // given : 포인트 충전을 위한 값 설정
        Point point = Point.create(1L);

        // when // then : 0이하의 값으로 충전할 경우 예외 발생
        assertThatThrownBy(() -> point.charge(new ChargePoint(1L, -1L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(BusinessLogicMessage.CHARGE_POINT_NOT_POSITIVE.getMessage());
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void usePointTest() {
        // given
        Point point = Point.of(1L, 1L, 3000L);

        // when
        point.use(1000L);

        // then : 0이하의 값으로 충전할 경우 예외 발생
        assertThat(point.getId()).isEqualTo(1L);
        assertThat(point.getMemberId()).isEqualTo(1L);
        assertThat(point.getPoint()).isEqualTo(2000L);
    }

}