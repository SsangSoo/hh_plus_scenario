package kr.hhplus.be.server.payment.application.dto.response;

import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.Getter;

public record PaymentResponse(
    Long id,
    Long orderId,
    Long totalAmount,
    String paymentState
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getOrderId(), payment.getTotalAmount(), payment.getPaymentState().name());
    }

}
