package kr.hhplus.be.server.coupon.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "COUPON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponJpaEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "coupon", nullable = false, updatable = false)
    private String coupon;

    @Column(name = "expiry_date", nullable = false, updatable = false)
    private LocalDate expiryDate;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "discountRate", nullable = false, updatable = false)
    private Integer discountRate;

    public static CouponJpaEntity from(Coupon coupon) {
        CouponJpaEntity  couponJpaEntity = new CouponJpaEntity();

        couponJpaEntity.coupon = coupon.getCoupon();
        couponJpaEntity.expiryDate = coupon.getExpiryDate();
        couponJpaEntity.amount = coupon.getAmount();
        couponJpaEntity.discountRate = coupon.getDiscountRate();

        return couponJpaEntity;
    }

    public Coupon toDomain() {
        return Coupon.of(
                id,
                coupon,
                expiryDate,
                amount,
                discountRate
        );
    }





}
