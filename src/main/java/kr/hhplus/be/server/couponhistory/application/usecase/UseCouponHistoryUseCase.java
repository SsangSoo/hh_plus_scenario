package kr.hhplus.be.server.couponhistory.application.usecase;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;

public interface UseCouponHistoryUseCase {

    void couponUse(CouponHistory couponHistory);

    void couponUseRollback(CouponHistory couponHistory);

}
