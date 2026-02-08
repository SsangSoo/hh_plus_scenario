package kr.hhplus.be.server.couponhistory.application.service;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.couponhistory.application.usecase.RegisterCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterCouponHistoryService implements RegisterCouponHistoryUseCase {

    private final CouponHistoryRepository couponHistoryRepository;
    private final RedisUtil redisUtil;


    @Override
    @Transactional
    public CouponHistory register(Long couponId, Long memberId) {
        CouponHistory couponHistory = CouponHistory.create(couponId, memberId);

        CouponHistory registeredCouponHistory = couponHistoryRepository.register(couponHistory);

        redisUtil.delete("coupon:" + couponId + ":member:" + memberId);

        return registeredCouponHistory;
    }
}
