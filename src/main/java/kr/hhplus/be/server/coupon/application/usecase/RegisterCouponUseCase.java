package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;

public interface RegisterCouponUseCase {

    CouponResponse register(RegisterCouponServiceRequest reqeust);
}
