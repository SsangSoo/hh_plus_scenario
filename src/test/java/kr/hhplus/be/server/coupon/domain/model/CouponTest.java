package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("쿠폰 모델 생성테스트")
    void createTest() {
        Coupon coupon = Coupon.create("123asd", LocalDate.now(), 100, 10);

        assertThat(coupon.getId()).isNull();
        assertThat(coupon.getCoupon()).isEqualTo("123asd");
        assertThat(coupon.getDiscountRate()).isEqualTo(10);
        assertThat(coupon.getAmount()).isEqualTo(100);
    }

    @Test
    @DisplayName("쿠폰 of 테스트")
    void ofTest() {
        Coupon coupon = Coupon.of(1L, "asd123", LocalDate.now(), 100, 10);

        assertThat(coupon.getId()).isEqualTo(1L);
        assertThat(coupon).isNotNull();
    }

    @Test
    @DisplayName("쿠폰에 id 넣기")
    void assignIdTest() {
        Coupon coupon = Coupon.create("123asd", LocalDate.now(), 100, 10);

        assertThat(coupon.getId()).isNull();

        coupon.assignId(1L);

        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getId()).isEqualTo(1L);

    }


    @Test
    @DisplayName("총 금액을 입력받으면, 할인된 금액을 반환한다.")
    void calculateDiscountRateTest() {
        Long totalAmount = 10000L;

        Coupon coupon = Coupon.create("123asd", LocalDate.now(), 100, 10);

        Long calculatedAmount = coupon.calculateDiscountRate(totalAmount);

        assertThat(calculatedAmount).isEqualTo(1000L);
    }


    @Test
    @DisplayName("쿠폰의 개수가 0이면 발행할 수 없다.")
    void issueTest() {
        Coupon coupon = Coupon.of(1L, "asd123", LocalDate.now(), 1, 10);

        coupon.issue();

        assertThatThrownBy(() -> coupon.issue())
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER.getMessage());
    }

    @Test
    @DisplayName("쿠폰 유효기간이 지나면 사용할 수 없다.")
    void expiryDateValidateTest() {
        Coupon coupon = Coupon.of(1L, "asd123", LocalDate.now().minusDays(1L), 1, 10);

        assertThatThrownBy(() -> coupon.verifyUsable())
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.USABLE_VALIDITY_PERIOD_HAS_PASSED.getMessage());
    }


}