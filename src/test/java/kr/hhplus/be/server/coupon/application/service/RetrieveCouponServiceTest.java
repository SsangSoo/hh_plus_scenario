package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.usecase.RetrieveCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RetrieveCouponServiceTest {

    @Mock
    CouponRepository couponRepository;

    RetrieveCouponUseCase retrieveCouponUseCase;

    @BeforeEach
    void setUp() {
        retrieveCouponUseCase = new RetrieveCouponService(couponRepository);
    }


    @Test
    @DisplayName("쿠폰을 조회할 수 있다.")
    void retrieveCouponTest() {
        // given
        Coupon coupon = Coupon.create("123456789012345", LocalDate.now().plusDays(3L), 10, 10);
        coupon.assignId(1L);

        given(couponRepository.retrieve(anyLong())).willReturn(coupon);

        // when
        CouponResponse retrieved = retrieveCouponUseCase.retrieve(1L);

        // then
        assertThat(retrieved.getCouponId()).isEqualTo(coupon.getId());
        assertThat(retrieved.getCoupon()).isEqualTo(coupon.getCoupon());
        assertThat(retrieved.getAmount()).isEqualTo(coupon.getAmount());
        assertThat(retrieved.getExpiryDate()).isEqualTo(coupon.getExpiryDate());
        assertThat(retrieved.getDiscountRate()).isEqualTo(coupon.getDiscountRate());

    }

}