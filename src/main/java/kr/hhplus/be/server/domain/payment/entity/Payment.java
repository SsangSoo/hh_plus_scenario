package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Column(name = "id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "discount")
    private Long discount;

    @Column(name = "final_amount")
    private Long finalAmount;

    @Column(name = "payment_state")
    private PaymentState paymentState;

    enum PaymentState {
        PAYMENT_COMPLETE,
        PAYMENT_CANCEL;

    }
}
