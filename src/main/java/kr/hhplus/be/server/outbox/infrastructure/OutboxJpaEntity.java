package kr.hhplus.be.server.outbox.infrastructure;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.outbox.domain.model.Outbox;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxJpaEntity extends BaseEntity {

    @Id
    private Long orderId;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "order_date")
    private LocalDate orderDate;

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
                orderId,
                paymentMethod,
                orderDate,
                paymentState
        );
    }


    public static OutboxJpaEntity from(Outbox outbox) {
        return new OutboxJpaEntity(
                outbox.getOrderId(),
                outbox.getPaymentMethod(),
                outbox.getOrderDate(),
                outbox.getPaymentState()
        );
    }

    private OutboxJpaEntity(Long orderId, PaymentMethod paymentMethod, LocalDate orderDate, PaymentState paymentState) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.paymentState = paymentState;
    }
}
