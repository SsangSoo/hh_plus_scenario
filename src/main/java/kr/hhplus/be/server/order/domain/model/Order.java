package kr.hhplus.be.server.order.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Order {

    private Long id;
    private Long memberId;
    private LocalDateTime orderDate;

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


