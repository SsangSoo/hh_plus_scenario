package kr.hhplus.be.server.orderproduct.entity;


import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.orderproduct.service.request.OrderProductServiceRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private Long orderId;

    @Column(name = "quantity", nullable = false, updatable = false)
    private Long quantity;

    public static OrderProduct register(OrderProductServiceRequest request, Long orderId) {
        return new OrderProduct(request.productId(), request.quantity(), orderId);
    }

    private OrderProduct(Long productId, Long quantity, Long orderId) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderId = orderId;
        this.createdDate = LocalDateTime.now();
    }
}
