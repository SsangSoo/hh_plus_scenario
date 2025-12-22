package kr.hhplus.be.server.order.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Order {

    private Long id;
    private Long memberId;
    private LocalDateTime orderDate;

    public Order() {}

    private Order(Long id, Long memberId, LocalDateTime orderDate) {
        this.id = id;
        this.memberId = memberId;
        this.orderDate = orderDate;
    }

    public static Order of(Long id, Long memberId, LocalDateTime orderDate) {
        return new Order(id, memberId, orderDate);
    }

    public static Order create(Long memberId) {
        Order order = new Order();
        order.memberId = memberId;
        order.orderDate = LocalDateTime.now();
        return order;
    }

    public void assignId(Long id) {
        this.id = id;
    }


}


