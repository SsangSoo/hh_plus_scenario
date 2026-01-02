package kr.hhplus.be.server.couponhistory.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponHistory {

    private Long id;
    private Long couponId;
    private Long memberId;
    private LocalDateTime couponIssuance;
    private Boolean couponUsed;

    private CouponHistory() {}

    private CouponHistory(Long id, Long couponId, Long memberId, LocalDateTime couponIssuance, Boolean couponUsed) {
        this.id = id;
        this.couponId = couponId;
        this.memberId = memberId;
        this.couponIssuance = couponIssuance;
        this.couponUsed = couponUsed;
    }

    public static CouponHistory of(Long id, Long couponId, Long memberId, LocalDateTime couponIssuance, Boolean couponUsed) {
        return new CouponHistory(id, couponId, memberId, couponIssuance, couponUsed);
    }

    public static CouponHistory create(Long couponId, Long memberId) {
        CouponHistory couponHistory = new CouponHistory();

        couponHistory.couponId = couponId;
        couponHistory.memberId = memberId;

        couponHistory.couponIssuance = LocalDateTime.now();
        couponHistory.couponUsed = false;

        return couponHistory;
    }

    public void assignId(Long id) {
        this.id = id;
    }

}
