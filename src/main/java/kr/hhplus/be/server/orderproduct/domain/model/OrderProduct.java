package kr.hhplus.be.server.orderproduct.domain.model;

import lombok.Getter;

@Getter
public class OrderProduct {

    private Long id;
    private Long productId;
    private Long orderId;
    private Long quantity;


    public static OrderProduct of(Long id, Long productId, Long orderId, Long quantity) {
        return new OrderProduct(id, productId, orderId, quantity);
    }

    public static OrderProduct create(Long productId, Long orderId, Long quantity) {
        return new OrderProduct(productId, orderId, quantity);
    }

    private OrderProduct(Long id, Long productId, Long orderId, Long quantity) {
        this.id = id;
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    private OrderProduct(Long productId, Long orderId, Long quantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}
