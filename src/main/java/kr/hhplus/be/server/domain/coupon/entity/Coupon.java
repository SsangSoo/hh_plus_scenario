package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "coupon", nullable = false)
    private String coupon;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Version
    @Column(name = "coupon_version", nullable = false)
    private Long couponVersion;

    @Column(name = "create_date",  nullable = false)
    private LocalDateTime createDate;

    @Column(name = "modified_date",  nullable = false)
    private LocalDateTime modifiedDate;




    public Long getId() {
        return id;
    }

    public String getCoupon() {
        return coupon;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getCouponVersion() {
        return couponVersion;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
