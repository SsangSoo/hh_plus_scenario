package kr.hhplus.be.server.payment.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentJpaEntity extends BaseEntity {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_state")
    private PaymentState paymentState;


    public static PaymentJpaEntity from(Payment payment) {
        PaymentJpaEntity paymentJpaEntity = new PaymentJpaEntity();

        paymentJpaEntity.orderId = payment.getOrderId();
        paymentJpaEntity.totalAmount = payment.getTotalAmount();

        paymentJpaEntity.paymentMethod = payment.getPaymentMethod();

        paymentJpaEntity.paymentState = payment.getPaymentState();

        paymentJpaEntity.createdDate = LocalDateTime.now();
        paymentJpaEntity.modifiedDate = paymentJpaEntity.createdDate;
        paymentJpaEntity.removed = false;

        return paymentJpaEntity;
    }

    public Payment toDomain() {
        return Payment.of(
                id,
                orderId,
                totalAmount,
                paymentMethod,
                paymentState
        );
    }

    public void changeState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }
}
