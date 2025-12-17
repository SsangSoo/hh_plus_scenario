package kr.hhplus.be.server.domain.order.controller.request;

import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidOrderRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "memberId는 필수여야 한다.",
                        new OrderRequest(null, new OrderProductRequest(1L, 1L), "POINT"),
                        "memberId",
                        "사용자 Id는 필수입니다."
                ),
                Arguments.of(
                        "memberId는 0보다 커야한다.",
                        new OrderRequest(0L, new OrderProductRequest(1L, 1L), "POINT"),
                        "memberId",
                        "유효하지 않은 값입니다. 사용자 Id를 확인해주세요."
                ),
                Arguments.of(
                        "memberId는 0보다 커야한다.",
                        new OrderRequest(-1L, new OrderProductRequest(1L, 1L), "POINT"),
                        "memberId",
                        "유효하지 않은 값입니다. 사용자 Id를 확인해주세요."
                ),
                Arguments.of(
                        "결제방식은 필수여야 한다.",
                        new OrderRequest(1L, new OrderProductRequest(1L, 1L), null),
                        "paymentMethod",
                        "결제 방식은 필수입니다."
                ),
                Arguments.of(
                        "결제 방식은 올바른 값을 받아야 한다.",
                        new OrderRequest(1L, new OrderProductRequest(1L, 1L), "POINTA"),
                        "paymentMethod",
                        "올바르지 않은 결제방식입니다. 결제 방식을 확인해주세요"
                )
        );
    }
}
