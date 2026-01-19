package kr.hhplus.be.server.outbox.infrastructure;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxJpaEntity extends BaseEntity {

    @Id
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "payment_state")
    private PaymentState paymentState;

    public void paymentComplete() {
        paymentState = PaymentState.PAYMENT_COMPLETE;
    }

    public void cancel() {
        paymentState = PaymentState.PAYMENT_CANCEL;
    }


    public Outbox toDomain() {
        return Outbox.of(
                paymentId,
                orderId,
                totalAmount,
                paymentState
        );
    }


    public static OutboxJpaEntity from(Outbox outbox) {
        return new OutboxJpaEntity(
                outbox.getPaymentId(),
                outbox.getOrderId(),
                outbox.getTotalAmount(),
                outbox.getPaymentState()
        );
    }

    private OutboxJpaEntity(Long paymentId, Long orderId, Long totalAmount, PaymentState paymentState) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentState = paymentState;
    }
}
