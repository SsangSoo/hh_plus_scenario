package kr.hhplus.be.server.domain.orderproduct.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Long quantity;

    @Column(name = "created_date", nullable = false,  updatable = false)
    private LocalDateTime createdDate;

    public static OrderProduct register(Long productId, Long orderId, Long quantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.productId = productId;
        orderProduct.orderId = orderId;
        orderProduct.quantity = quantity;
        orderProduct.createdDate = LocalDateTime.now();
        return orderProduct;
    }
}
