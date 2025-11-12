package kr.hhplus.be.server.domain.couponhistory.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CouponHistory {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "coupon_issuance", nullable = false)
    private LocalDateTime couponIssuance;

    @Column(name = "coupon_used", nullable = false)
    private Boolean couponUsed;

    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;



    public Long getId() {
        return id;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getCouponIssuance() {
        return couponIssuance;
    }

    public Boolean getCouponUsed() {
        return couponUsed;
    }

    public LocalDateTime getModified() {
        return modified;
    }


}
