package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterCouponService implements RegisterCouponUseCase {

    private final CouponRepository couponRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public CouponResponse register(RegisterCouponServiceRequest reqeust) {
        Coupon coupon = couponRepository.save(Coupon.create(reqeust.coupon(), reqeust.expiryDate(), reqeust.amount(), reqeust.discountRate()));

        redisUtil.set(
                "coupon:" + coupon.getId(), // 쿠폰 키
                String.valueOf(coupon.getAmount()),                                         // 쿠폰 수량
                Duration.between(LocalDateTime.now(), coupon.getExpiryDate().plusDays(1L).atStartOfDay())
        );

        return CouponResponse.from(coupon);
    }


}
