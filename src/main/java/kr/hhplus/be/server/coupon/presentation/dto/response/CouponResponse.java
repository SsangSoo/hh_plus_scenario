package kr.hhplus.be.server.coupon.presentation.dto.response;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {

    private Long id;
    private String coupon;
    private LocalDate expiryDate;
    private Integer amount;
    private Integer discountRate;

    private CouponResponse(Long id, String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        this.id = id;
        this.coupon = coupon;
        this.expiryDate = expiryDate;
        this.amount = amount;
        this.discountRate = discountRate;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getCoupon(), coupon.getExpiryDate(), coupon.getAmount(), coupon.getDiscountRate());
    }

}
