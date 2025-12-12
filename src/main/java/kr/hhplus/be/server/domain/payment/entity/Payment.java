package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.domain.order.interfaces.web.request.PaymentMethod;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private Long totalAmount;


    @Column(name = "payment_state")
    private PaymentState paymentState;


    public static Payment register(PaymentServiceRequest request) {
        return register(request.orderId(), request.totalAmount(), request.paymentMethod());
    }

    private static Payment register(Long orderId, Long totalAmount, PaymentMethod paymentMethod) {
        Payment payment = new Payment();

        payment.orderId = orderId;
        payment.totalAmount = totalAmount;

        payment.createdDate = LocalDateTime.now();
        payment.modifiedDate = payment.createdDate;

        switch (paymentMethod) {
            case POINT -> payment.paymentState = PaymentState.PAYMENT_COMPLETE;
            case CREDIT_CARD -> payment.paymentState = PaymentState.PENDING;
            case BANK_TRANSFER ->  payment.paymentState = PaymentState.PENDING;
        }

        return payment;
    }
}
