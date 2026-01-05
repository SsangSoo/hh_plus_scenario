package kr.hhplus.be.server.outbox.domain.model;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Outbox {

    private Long paymentId;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private Long totalAmount;
    private PaymentState paymentState;


    public static Outbox of(Long paymentId, Long orderId, PaymentMethod paymentMethod, Long totalAmount, PaymentState paymentState) {
        return new Outbox(paymentId, orderId, paymentMethod, totalAmount, paymentState);
    }

    private Outbox(Long paymentId, Long orderId, PaymentMethod paymentMethod, Long totalAmount, PaymentState paymentState) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.paymentState = paymentState;
    }
}
