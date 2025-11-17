package kr.hhplus.be.server.domain.orderproduct.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private Long orderId;

    @Column(name = "quantity", nullable = false, updatable = false)
    private Integer quantity;


    public OrderProduct(Long productId, Long orderId, Integer quantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
    }
}
