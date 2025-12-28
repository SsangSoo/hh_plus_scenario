package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.usecase.RetrieveCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveCouponService implements RetrieveCouponUseCase {

    private final CouponRepository couponsRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponResponse retrieve(Long couponId) {
        Coupon coupon = couponsRepository.retrieve(couponId);

        return CouponResponse.from(coupon);
    }
}
