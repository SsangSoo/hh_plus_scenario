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


    public Order(Long memberId) {
        this.memberId = memberId;
        this.orderDate = LocalDateTime.now();
    }

    public static Order rigester(Long memberId) {
        Order order = new Order();
        order.memberId = memberId;
        order.orderDate = LocalDateTime.now();
        return order;
    }

}
