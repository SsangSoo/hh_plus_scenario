package kr.hhplus.be.server.coupon.application.usecase;

import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;

public interface IssueCouponUseCase {

    IssueCouponResponse issue(IssueCouponServiceRequest serviceRequest);

}
