package kr.hhplus.be.server.domain.order.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseTimeEntity;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;


    public static OrderEntity from(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.memberId = order.getMemberId();
        orderEntity.orderDate = order.getOrderDate();
        orderEntity.createdDate =  LocalDateTime.now();
        orderEntity.modifiedDate = orderEntity.createdDate;
        return orderEntity;


    }
}
