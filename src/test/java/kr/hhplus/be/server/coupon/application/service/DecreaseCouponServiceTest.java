package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class DecreaseCouponServiceTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        couponHistoryJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("decreaseCouponUseCase 테스트")
    void decreaseCouponUseCaseTest() {
        // given
        CouponResponse registeredCoupon = registerCouponUseCase.register(new RegisterCouponServiceRequest("coupon", LocalDate.now().plusDays(3L), 200, 10));

        CouponResponse couponResponse = retrieveCouponUseCase.retrieve(registeredCoupon.getCouponId());

        assertThat(couponResponse.getAmount()).isEqualTo(200);

        // when
        decreaseCouponUseCase.decrease(couponResponse.getCouponId());


        // then
        CouponResponse couponResponseAfterDecrease = retrieveCouponUseCase.retrieve(registeredCoupon.getCouponId());
        assertThat(couponResponseAfterDecrease.getAmount()).isEqualTo(199);

    }


}