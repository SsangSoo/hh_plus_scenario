package kr.hhplus.be.server.payment.domain.model;

import lombok.Getter;

@Getter
public class Payment {

    private Long id;                    // 결제 Id
    private Long orderId;               // 주문 Id
    private Long totalAmount;           // 총 금액
    private PaymentState paymentState;  // 결제 상태

    public static Payment of(Long id,  Long orderId, Long totalAmount, PaymentState paymentState) {
        return new Payment(id, orderId, totalAmount, paymentState);
    }

    public static Payment create(Long orderId, Long totalAmount) {
        return new Payment(orderId, totalAmount);
    }

    private Payment(Long orderId, Long totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentState = PaymentState.PENDING;
    }

    private Payment(Long id, Long orderId, Long totalAmount, PaymentState paymentState) {
        this.id = id;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentState = paymentState;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void discountAmount(Long discountAmount) {
        this.totalAmount -= discountAmount;
    }


    public void changeState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

}


