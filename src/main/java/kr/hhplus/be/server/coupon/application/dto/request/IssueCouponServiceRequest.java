package kr.hhplus.be.server.coupon.application.dto.request;

public record IssueCouponServiceRequest(
        Long couponId,
        Long memberId
) {
}
