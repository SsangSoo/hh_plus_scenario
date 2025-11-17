package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    //    @Column(name = "trasaction_status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private TransactionStatus transactionStatus;
//
//    @Column(name = "amount", nullable = false)
//    private Long amount;
//
//    @Column(name = "discount", nullable = false)
//    private Long discount;
//
//    @Column(name = "tatal_amount", nullable = false)
//    private Long totalAmount;
//
//    @Column(name = "state", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private OrderState orderState;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

//    enum TransactionStatus {
//        REFUND, PURCHASE;
//    }
//
//    enum OrderState {
//        PAYMENT, COMPLETE;
//    }

    public Order(Long memberId) {
        this.memberId = memberId;
        this.orderDate = LocalDateTime.now();
        this.modifiedDate = this.orderDate;
    }
}
