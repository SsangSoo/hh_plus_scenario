package kr.hhplus.be.server.coupon.presentation.dto.response;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {

    private Long couponId;
    private String coupon;
    private String expiryDate;
    private Integer amount;
    private Integer discountRate;

    private CouponResponse(Long couponId, String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        this.couponId = couponId;
        this.coupon = coupon;
        this.expiryDate = expiryDate.toString();
        this.amount = amount;
        this.discountRate = discountRate;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getCoupon(), coupon.getExpiryDate(), coupon.getAmount(), coupon.getDiscountRate());
    }

}
