package kr.hhplus.be.server.couponhistory.application.usecase;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;

public interface RetrieveCouponHistoryUseCase {

    CouponHistory retrieveCouponHistory(Long memberId, Long couponId);
}
