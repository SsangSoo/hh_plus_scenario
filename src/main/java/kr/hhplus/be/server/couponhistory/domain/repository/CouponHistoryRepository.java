package kr.hhplus.be.server.couponhistory.domain.repository;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;

import java.util.Optional;

public interface CouponHistoryRepository {

    CouponHistory register(CouponHistory couponHistory);

    Optional<CouponHistory> retrieveCouponHistory(Long memberId, Long couponId);
}
