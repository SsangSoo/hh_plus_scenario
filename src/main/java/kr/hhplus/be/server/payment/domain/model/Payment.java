package kr.hhplus.be.server.payment.domain.model;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import lombok.Getter;

@Getter
public class Payment {

    private Long id;
    private Long orderId;
    private Long totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentState paymentState;

    public static Payment of(Long id,  Long orderId, Long totalAmount, PaymentMethod paymentMethod, PaymentState paymentState) {
        return new Payment(id, orderId, totalAmount, paymentMethod, paymentState);
    }

    public static Payment create(Long orderId, Long totalAmount, PaymentMethod paymentMethod) {
        return new Payment(orderId, totalAmount, paymentMethod);
    }

    private Payment(Long orderId, Long totalAmount, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentState = PaymentState.PENDING;
    }

    private Payment(Long id, Long orderId, Long totalAmount, PaymentMethod paymentMethod, PaymentState paymentState) {
        this.id = id;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
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


