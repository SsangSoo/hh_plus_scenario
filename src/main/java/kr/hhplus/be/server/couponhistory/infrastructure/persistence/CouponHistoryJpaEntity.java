package kr.hhplus.be.server.couponhistory.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "COUPON_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponHistoryJpaEntity {

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
    private boolean couponUsed;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;


    public CouponHistory toDomain() {
        return CouponHistory.of(
                id,
                couponId,
                memberId,
                couponIssuance,
                couponUsed
        );
    }


    public static CouponHistoryJpaEntity from(CouponHistory couponHistory) {
        CouponHistoryJpaEntity couponHistoryJpaEntity = new CouponHistoryJpaEntity();
        
        couponHistoryJpaEntity.couponId = couponHistory.getCouponId();
        couponHistoryJpaEntity.memberId = couponHistory.getMemberId();
        couponHistoryJpaEntity.couponIssuance = couponHistory.getCouponIssuance();
        couponHistoryJpaEntity.couponUsed = couponHistory.isCouponUsed();
        
        couponHistoryJpaEntity.modifiedDate = LocalDateTime.now();
        return couponHistoryJpaEntity;
    }

    
}
