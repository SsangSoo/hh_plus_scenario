package kr.hhplus.be.server.order.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.order.domain.model.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity extends BaseTimeEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    public Order toDomain() {
        return Order.of(id, memberId, orderDate);
    }

    public static OrderJpaEntity from(Order order) {
        OrderJpaEntity orderEntity = new OrderJpaEntity();
        orderEntity.memberId = order.getMemberId();
        orderEntity.orderDate = order.getOrderDate();
        orderEntity.createdDate =  LocalDateTime.now();
        orderEntity.modifiedDate = orderEntity.createdDate;
        return orderEntity;
    }
}
