package kr.hhplus.be.server.payment.presentation.dto;

import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidPaymentRequestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "결제시 주문 Id는 필수여야 한다.",
                        new PaymentRequest(null, 1L, 1L, 1L),
                        "orderId",
                        "주문 Id는 필수입니다."
                ),
                Arguments.of(
                        "결제시 회원 Id는 필수여야 한다.",
                        new PaymentRequest(1L, null, 1L, 1L),
                        "memberId",
                        "회원 Id는 필수입니다."
                ),
                Arguments.of(
                        "결제시 결제 Id는 필수여야 한다.",
                        new PaymentRequest(1L, 1L, null, 1L),
                        "paymentId",
                        "결제 Id는 필수입니다."
                ),
                Arguments.of(
                        "결제시 주문 Id는 양수여야한다(0)",
                        new PaymentRequest(0L, 1L, 1L, 1L),
                        "orderId",
                        "유효하지 않은 값입니다. 주문 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 주문 Id는 양수여야한다(-1)",
                        new PaymentRequest(-1L, 1L, 1L, 1L),
                        "orderId",
                        "유효하지 않은 값입니다. 주문 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 회원 Id는 양수여야한다(0)",
                        new PaymentRequest(1L, 0L, 1L, 1L),
                        "memberId",
                        "유효하지 않은 값입니다. 회원 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 회원 Id는 양수여야한다(-1)",
                        new PaymentRequest(1L, -1L, 1L, 1L),
                        "memberId",
                        "유효하지 않은 값입니다. 회원 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 결제 Id는 양수여야한다(0)",
                        new PaymentRequest(1L, 1L, 0L, 1L),
                        "paymentId",
                        "유효하지 않은 값입니다. 결제 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 결제 Id는 양수여야한다(-1)",
                        new PaymentRequest(1L, 1L, -1L, 1L),
                        "paymentId",
                        "유효하지 않은 값입니다. 결제 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 쿠폰 Id는 양수여야한다(0)",
                        new PaymentRequest(1L, 1L, 1L, 0L),
                        "couponId",
                        "유효하지 않은 값입니다. 쿠폰 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제시 쿠폰 Id는 양수여야한다(-1)",
                        new PaymentRequest(1L, 1L, 1L, -1L),
                        "couponId",
                        "유효하지 않은 값입니다. 쿠폰 Id를 확인해주세요."
                )
        );
    }
}
