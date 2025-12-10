package kr.hhplus.be.server.domain.order.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.order.service.request.OrderServiceRequest;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;

public record OrderRequest(

        @Positive
        Long memberId,

        @Valid
        OrderProductRequest orderProductRequest,

        @NotBlank
        String paymentMethod
) {

    public OrderServiceRequest toServiceRequest() {
        try {
            PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException eae) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.UNSUPPORTED_PAYMENT_METHOD.getMessage());
        }
        return new OrderServiceRequest(
                memberId,
                orderProductRequest.toServiceRequest(),
                PaymentMethod.valueOf(paymentMethod)
        );
    }

}
