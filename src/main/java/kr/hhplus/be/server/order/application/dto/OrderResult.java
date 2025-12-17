package kr.hhplus.be.server.order.application.dto;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.payment.facade.service.response.PaymentResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResult {

    private Long orderId;
    private Long memberId;
    private LocalDateTime orderDate;
    private Long paymentId;
    private Long totalAmount;
    private String paymentState;


    public static OrderResult from(Order order, PaymentResponse paymentResponse) {
        OrderResult orderResponse = new OrderResult();

        orderResponse.orderId = order.getId();
        orderResponse.memberId = order.getMemberId();
        orderResponse.orderDate = order.getOrderDate().withNano(0);

        orderResponse.paymentId = paymentResponse.getId();
        orderResponse.totalAmount = paymentResponse.getTotalAmount();
        orderResponse.paymentState = paymentResponse.getPaymentState();

        return orderResponse;
    }
}
