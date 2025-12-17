package kr.hhplus.be.server.domain.order.application.out.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.domain.order.domain.model.Order;
import kr.hhplus.be.server.domain.payment.facade.service.response.PaymentResponse;
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


    public static OrderResponse from(Order order, PaymentResponse paymentResponse) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.orderId = order.getId();
        orderResponse.memberId = order.getMemberId();
        orderResponse.orderDate = order.getOrderDate().withNano(0);

        orderResponse.paymentId = paymentResponse.getId();
        orderResponse.totalAmount = paymentResponse.getTotalAmount();
        orderResponse.paymentState = paymentResponse.getPaymentState();

        return orderResponse;
    }
}
