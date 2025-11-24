package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.base.BaseEntity;
import kr.hhplus.be.server.domain.payment.service.request.PaymentServiceRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

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
        return register(request.orderId(), request.totalAmount());
    }

    private static Payment register(Long orderId, Long totalAmount) {
        Payment payment = new Payment();

        payment.orderId = orderId;
        payment.totalAmount = totalAmount;
        payment.paymentState = PaymentState.PAYMENT_COMPLETE;

        payment.createdDate = LocalDateTime.now();
        payment.modifiedDate = payment.createdDate;

        return payment;
    }
}
