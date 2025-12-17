package kr.hhplus.be.server.couponhistory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "COUPON_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponHistory {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false, updatable = false)
    private Long couponId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "coupon_issuance", nullable = false, updatable = false)
    private LocalDateTime couponIssuance;

    @Column(name = "coupon_used", nullable = false)
    private Boolean couponUsed;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    public CouponHistory(Long couponId, Long memberId, LocalDateTime couponIssuance, Boolean couponUsed) {
        this.couponId = couponId;
        this.memberId = memberId;
        this.couponIssuance = couponIssuance;
        this.couponUsed = couponUsed;
        this.modifiedDate = this.couponIssuance;
    }
}
