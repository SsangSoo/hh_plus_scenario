package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.usecase.DecreaseCouponUseCase;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecreaseCouponService implements DecreaseCouponUseCase {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public void decrease(Long couponId) {
        couponRepository.decrease(couponId);
    }
}
