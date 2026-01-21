package kr.hhplus.be.server.couponhistory.application.usecase;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;

public interface RegisterCouponHistoryUseCase {

    CouponHistory register(Long couponId, Long memberId);
}
