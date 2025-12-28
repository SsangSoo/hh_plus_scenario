package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Coupon {

    private Long id;
    private String coupon;
    private LocalDate expiryDate;
    private Integer amount;
    private Integer discountRate;


    public static Coupon create(String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        return new Coupon(coupon, expiryDate, amount, discountRate);
    }

    public static Coupon of(Long id, String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        return new Coupon(id, coupon, expiryDate, amount, discountRate);
    }


    public Long calculateDiscountRate(Long totalAmount) {
        return (long) (totalAmount - (totalAmount * (discountRate / 100d)));
    }

    public void issue() {
        if(this.amount == 0) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER);
        }
        this.amount--;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    private Coupon(Long id, String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        this.id = id;
        this.coupon = coupon;
        this.expiryDate = expiryDate;
        this.amount = amount;
        this.discountRate = discountRate;
    }

    private Coupon(String coupon, LocalDate expiryDate, Integer amount, Integer discountRate) {
        this.coupon = coupon;
        this.expiryDate = expiryDate;
        this.amount = amount;
        this.discountRate = discountRate;
    }
}
