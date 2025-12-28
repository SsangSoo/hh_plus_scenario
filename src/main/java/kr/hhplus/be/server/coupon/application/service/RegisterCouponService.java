package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterCouponService implements RegisterCouponUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponResponse register(RegisterCouponServiceRequest reqeust) {
        Coupon coupon = couponRepository.save(Coupon.create(reqeust.coupon(), reqeust.expiryDate(), reqeust.amount(), reqeust.discountRate()));

        return CouponResponse.from(coupon);
    }




}
