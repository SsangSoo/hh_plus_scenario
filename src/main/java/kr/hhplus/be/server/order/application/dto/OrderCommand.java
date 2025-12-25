package kr.hhplus.be.server.order.application.dto;

import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;

import java.util.List;

public record OrderCommand(
        Long memberId,
        List<OrderProductServiceRequest> orderProductsRequest,
        PaymentMethod paymentMethod
) {

}
