package kr.hhplus.be.server.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.order.application.dto.OrderResult;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.payment.facade.service.response.PaymentResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private Long orderId;
    private Long memberId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;
    private Long paymentId;
    private Long totalAmount;
    private String paymentState;

    public static OrderResponse from(OrderResult orderResult) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.orderId = orderResult.getOrderId();
        orderResponse.memberId = orderResult.getMemberId();
        orderResponse.orderDate = orderResult.getOrderDate().withNano(0);

        orderResponse.paymentId = orderResult.getPaymentId();
        orderResponse.totalAmount = orderResult.getTotalAmount();
        orderResponse.paymentState = orderResult.getPaymentState();

        return orderResponse;
    }
}
