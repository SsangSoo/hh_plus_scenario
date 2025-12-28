package kr.hhplus.be.server.coupon.application.dto.request;

import java.time.LocalDate;

public record RegisterCouponServiceRequest(
    String coupon,
    LocalDate expiryDate,
    Integer amount,
    Integer discountRate
) {

}
