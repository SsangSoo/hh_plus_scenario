package kr.hhplus.be.server.domain.product.controller.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.domain.product.service.request.RegisterProductServiceRequest;

public record RegisterProductRequest(

        @NotBlank(message = "상품 이름은 필수입니다.")
        String productName,

        @NotNull(message = "상품 등록시 가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0이상 이어야 합니다.")
        Long price
) {
    public RegisterProductServiceRequest toServiceRequest() {
        return new RegisterProductServiceRequest(productName, price);
    }
}
