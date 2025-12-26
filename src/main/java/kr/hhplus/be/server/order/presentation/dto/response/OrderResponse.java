package kr.hhplus.be.server.order.presentation.dto.response;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import lombok.Getter;

@Getter
public class OrderResponse {

    private Long orderId;
    private Long memberId;
    private String orderDate;
    private Long paymentId;
    private Long totalAmount;
    private String paymentState;


    public static OrderResponse from(Order order, PaymentResponse paymentResponse) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.orderId = order.getId();
        orderResponse.memberId = order.getMemberId();
        orderResponse.orderDate = order.getOrderDate().withNano(0).toString();

        orderResponse.paymentId = paymentResponse.getId();
        orderResponse.totalAmount = paymentResponse.getTotalAmount();
        orderResponse.paymentState = paymentResponse.getPaymentState();

        return orderResponse;
    }
}
