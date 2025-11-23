package kr.hhplus.be.server.domain.payment.service.response;

import jakarta.persistence.Column;
import kr.hhplus.be.server.domain.payment.entity.Payment;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private Long totalAmount;
    private Long discount;
    private Long finalAmount;
    private String paymentState;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();

        paymentResponse.id = payment.getId();
        paymentResponse.orderId = payment.getOrderId();
        paymentResponse.totalAmount = payment.getTotalAmount();
        paymentResponse.finalAmount = payment.getFinalAmount();
        paymentResponse.paymentState = payment.getPaymentState().name();

        return paymentResponse;

    }
}
