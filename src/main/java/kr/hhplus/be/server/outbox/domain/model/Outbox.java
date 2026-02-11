package kr.hhplus.be.server.outbox.domain.model;

import kr.hhplus.be.server.payment.domain.model.PaymentState;

public record Outbox(
        Long paymentId,
        Long orderId,
        Long totalAmount,
        PaymentState paymentState
) {

    public Outbox(Long paymentId, Long orderId, Long totalAmount, PaymentState paymentState) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentState = paymentState;
    }
}
