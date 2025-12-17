package kr.hhplus.be.server.domain.point.entity;

import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.service.request.ChargePoint;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class PointTest {

    @Test
    @DisplayName("회원의 Id로 Point는 생성된다. 그리고 생성된 Point는 0원이며, 삭제 여부는 false다. 그리고 생성일자와 수정일자가 동일해야 한다.")
    void registerPointWithMemberIdTest() {
        // given : 회원 Id 셋팅
        Long memberId = 1L;

        // when : 포인트 생성
        Point point = Point.register(memberId);

        // then : 포인트 생성 검증
        assertThat(point).isNotNull();
        assertThat(point.getId()).isNull();
        assertThat(point.getPoint()).isZero();
        assertThat(point.getDeleted()).isFalse();
        assertThat(point.getCreatedDate()).isEqualTo(point.getModifiedDate());
    }


    @Test
    @DisplayName("포인트를 삭제한다. 단 논리적 삭제이다.")
    void deletePointTest() {
        // given : 회원 Id 셋팅 및 포인트 생성
        Long memberId = 1L;
        Point point = Point.register(memberId);

        // when : 포인트 삭제
        point.delete();

        // then : 포인트 삭제 검증
        assertThat(point).isNotNull();
        assertThat(point.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePointTest() {
        // given : 포인트 충전을 위한 값 설정
        Long memberId = 1L;
        Point point = Point.register(memberId);

        ChargePoint chargePoint = new ChargePoint(memberId, 100L);

        // when : 포인트 충전
        point.charge(chargePoint);

        // then : 충전된 포인트 확인
        assertThat(point.getPoint()).isEqualTo(100L);
    }

    @Test
    @DisplayName("0이하의 값으로 포인트를 충전할 경우 예외가 발생한다.")
    void cannotChargePointNegativeTest() {
        // given : 포인트 충전을 위한 값 설정
        Long memberId = 1L;
        Point point = Point.register(memberId);

        // when // then : 0이하의 값으로 충전할 경우 예외 발생
        assertThatThrownBy(() -> point.charge(new ChargePoint(memberId, 0L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(BusinessLogicMessage.CHARGE_POINT_NOT_POSITIVE.getMessage());
    }

}