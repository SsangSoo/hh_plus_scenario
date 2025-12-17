package kr.hhplus.be.server.payment.facade.service.response;

import kr.hhplus.be.server.payment.entity.Payment;
import lombok.Getter;

@Getter
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private Long totalAmount;
    private String paymentState;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();

        paymentResponse.id = payment.getId();
        paymentResponse.orderId = payment.getOrderId();
        paymentResponse.totalAmount = payment.getTotalAmount();
        paymentResponse.paymentState = payment.getPaymentState().name();

        return paymentResponse;

    }
}
