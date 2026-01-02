package kr.hhplus.be.server.payment.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(

        @NotNull(message = "주문 Id는 필수입니다.")
        @Positive(message = "유효하지 않은 값입니다. 주문 Id를 확인해주세요.")
        Long orderId,

        @NotNull(message = "회원 Id는 필수입니다.")
        @Positive(message = "유효하지 않은 값입니다. 회원 Id를 확인해주세요.")
        Long memberId,

        @NotNull(message = "결제 Id는 필수입니다.")
        @Positive(message = "유효하지 않은 값입니다. 결제 Id를 확인해주세요.")
        Long paymentId,

        @Positive(message = "유효하지 않은 값입니다. 쿠폰 Id를 확인해주세요.")
        Long couponId
) {
}
