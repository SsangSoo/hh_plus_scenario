package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "trasaction_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "discount", nullable = false)
    private Long discount;

    @Column(name = "tatal_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    enum TransactionStatus {

        REFUND, PURCHASE;

    }

    enum OrderState {
        PAYMENT, COMPLETE;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getDiscount() {
        return discount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
