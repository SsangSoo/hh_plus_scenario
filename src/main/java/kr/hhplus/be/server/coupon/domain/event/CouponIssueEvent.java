package kr.hhplus.be.server.coupon.domain.event;

public record CouponIssueEvent(
        Long couponId,
        Long memberId
) {
}
