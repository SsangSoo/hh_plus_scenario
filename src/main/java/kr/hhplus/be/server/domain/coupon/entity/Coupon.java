package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "COUPON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "coupon", nullable = false, updatable = false)
    private String coupon;

    @Column(name = "expiry_date", nullable = false, updatable = false)
    private LocalDateTime expiryDate;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "create_date",  nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "modified_date",  nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "discountRate", nullable = false, updatable = false)
    private Integer discountRate;


    public Coupon(String coupon, LocalDateTime expiryDate, Long amount) {
        this(coupon, expiryDate, amount, 10);
    }

    public Coupon(String coupon, LocalDateTime expiryDate, Long amount, Integer discountRate) {
        this.coupon = coupon;
        this.expiryDate = expiryDate;
        this.amount = amount;
        this.createDate = LocalDateTime.now();
        this.modifiedDate = createDate;
        this.discountRate = discountRate;
    }



}
