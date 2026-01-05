package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;

public interface RetrieveCouponUseCase {

    CouponResponse retrieve(Long couponId);

}
