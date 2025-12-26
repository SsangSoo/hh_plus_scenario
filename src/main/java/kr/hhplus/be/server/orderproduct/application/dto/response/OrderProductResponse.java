package kr.hhplus.be.server.orderproduct.application.dto.response;

import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.infrastructure.persistence.OrderProductJpaEntity;
import lombok.Getter;

@Getter
public class OrderProductResponse {

    private Long productId;
    private Long orderId;
    private Long quantity;


    public static OrderProductResponse from(OrderProduct orderProduct) {
        OrderProductResponse orderProductResponse = new OrderProductResponse();
        orderProductResponse.productId =  orderProduct.getProductId();
        orderProductResponse.orderId = orderProduct.getOrderId();
        orderProductResponse.quantity = orderProduct.getQuantity();
        return orderProductResponse;
    }

}
