package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.request.RegisterCouponRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RegisterCouponServiceTest {


    @Mock
    CouponRepository couponRepository;


    RegisterCouponUseCase registerCouponUseCase;


    @BeforeEach
    void setUp() {
        registerCouponUseCase = new RegisterCouponService(couponRepository);
    }

    @Test
    @DisplayName("쿠폰 생성 비즈니스 로직 테스트")
    void registerCouponTest() {
        // given
        RegisterCouponRequest request = new RegisterCouponRequest("123456789012345", LocalDate.now().plusDays(3L), 10, 10);
        RegisterCouponServiceRequest serviceRequest = request.toServiceRequest();

        Coupon coupon = Coupon.create(request.coupon(), request.expiryDate(), request.amount(), request.discountRate());
        coupon.assignId(1L);

        given(couponRepository.save(any())).willReturn(coupon);

        // when
        CouponResponse couponResponse = registerCouponUseCase.register(serviceRequest);


        // then
        Assertions.assertThat(couponResponse.getId()).isEqualTo(coupon.getId());
        Assertions.assertThat(couponResponse.getAmount()).isEqualTo(coupon.getAmount());
        Assertions.assertThat(couponResponse.getCoupon()).isEqualTo(coupon.getCoupon());
        Assertions.assertThat(couponResponse.getExpiryDate()).isEqualTo(coupon.getExpiryDate());
        Assertions.assertThat(couponResponse.getDiscountRate()).isEqualTo(coupon.getDiscountRate());



    }


}