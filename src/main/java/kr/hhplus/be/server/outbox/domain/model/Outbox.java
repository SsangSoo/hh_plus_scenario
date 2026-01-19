package kr.hhplus.be.server.outbox.domain.model;

import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.Getter;

@Getter
public class Outbox {

    private Long paymentId;
    private Long orderId;
    private Long totalAmount;
    private PaymentState paymentState;


    public static Outbox of(Long paymentId, Long orderId, Long totalAmount, PaymentState paymentState) {
        return new Outbox(paymentId, orderId, totalAmount, paymentState);
    }

    private Outbox(Long paymentId, Long orderId, Long totalAmount, PaymentState paymentState) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentState = paymentState;
    }
}
