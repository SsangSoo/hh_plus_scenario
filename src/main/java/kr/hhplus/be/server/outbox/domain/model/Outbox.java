package kr.hhplus.be.server.outbox.domain.model;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Outbox {

    private Long orderId;
    private PaymentMethod paymentMethod;
    private LocalDate orderDate;
    private PaymentState paymentState;


    public static Outbox of(Long orderId, PaymentMethod paymentMethod, LocalDate orderDate, PaymentState paymentState) {
        return new Outbox(orderId, paymentMethod, orderDate, paymentState);
    }

    private Outbox(Long orderId, PaymentMethod paymentMethod, LocalDate orderDate, PaymentState paymentState) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.paymentState = paymentState;
    }
}
