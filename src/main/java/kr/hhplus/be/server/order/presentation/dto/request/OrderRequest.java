package kr.hhplus.be.server.order.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.common.validation.ValidEnum;

import java.util.List;

public record OrderRequest(

        @NotNull(message = "사용자 Id는 필수입니다.")
        @Positive(message = "유효하지 않은 값입니다. 사용자 Id를 확인해주세요.")
        Long memberId,

        @Valid
        List<OrderProductRequest> orderProductsRequest,

        @NotBlank(message = "결제 방식은 필수입니다.")
        @ValidEnum(
                enumClass = PaymentMethod.class,
                message = "올바르지 않은 결제방식입니다. 결제 방식을 확인해주세요")
        String paymentMethod
) {

    public OrderCommand toOrderCommand() {
        return new OrderCommand(
                memberId,
                orderProductsRequest.stream()
                        .map(OrderProductRequest::toOrderProductCommand)
                        .toList(),
                PaymentMethod.valueOf(paymentMethod)
        );
    }

}
