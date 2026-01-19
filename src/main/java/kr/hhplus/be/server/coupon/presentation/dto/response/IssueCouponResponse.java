package kr.hhplus.be.server.coupon.presentation.dto.response;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssueCouponResponse {

    private Long couponHistoryId;
    private Long couponId;
    private Long memberId;
    private String couponIssuance;
    private boolean couponUsed;

    public static IssueCouponResponse from(CouponHistory couponHistory) {
        return new IssueCouponResponse(
                couponHistory.getId(),
                couponHistory.getCouponId(),
                couponHistory.getMemberId(),
                couponHistory.getCouponIssuance(),
                couponHistory.isCouponUsed()
        );
    }

    private IssueCouponResponse(Long couponHistoryId, Long couponId, Long memberId, LocalDateTime couponIssuance, boolean couponUsed) {
        this.couponHistoryId = couponHistoryId;
        this.couponId = couponId;
        this.memberId = memberId;
        this.couponIssuance = couponIssuance.withNano(0).toString();
        this.couponUsed = couponUsed;
    }
}
