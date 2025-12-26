package kr.hhplus.be.server.orderproduct.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductJpaEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private Long orderId;

    @Column(name = "quantity", nullable = false, updatable = false)
    private Long quantity;

    public static OrderProductJpaEntity from(OrderProduct orderProduct) {
        OrderProductJpaEntity orderProductJpaEntity = new OrderProductJpaEntity();
        orderProductJpaEntity.productId = orderProduct.getProductId();
        orderProductJpaEntity.orderId = orderProduct.getOrderId();
        orderProductJpaEntity.quantity = orderProduct.getQuantity();

        orderProductJpaEntity.createdDate = LocalDateTime.now();
        orderProductJpaEntity.modifiedDate = orderProductJpaEntity.createdDate;
        return orderProductJpaEntity;
    }

    public OrderProduct toDomain() {
        return OrderProduct.of(
                id,
                productId,
                orderId,
                quantity
        );
    }
}
