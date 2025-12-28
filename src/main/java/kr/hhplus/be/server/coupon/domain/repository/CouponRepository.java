package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.Coupon;

import java.util.List;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    List<Coupon> saveList(List<Coupon> coupons);

    Coupon retrieve(Long couponId);
}
