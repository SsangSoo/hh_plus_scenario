package kr.hhplus.be.server.payment.domain.event;

import kr.hhplus.be.server.payment.domain.model.PaymentState;

public record PaymentEvent(
        Long paymentId,
        Long orderId)
{
    public PaymentEvent(Long paymentId, Long orderId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

}
