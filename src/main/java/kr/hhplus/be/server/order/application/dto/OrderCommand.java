package kr.hhplus.be.server.order.application.dto;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.orderproduct.service.request.OrderProductServiceRequest;

public record OrderCommand(
        Long memberId,
        OrderProductServiceRequest orderProductRequest,
        PaymentMethod paymentMethod
) {

}
