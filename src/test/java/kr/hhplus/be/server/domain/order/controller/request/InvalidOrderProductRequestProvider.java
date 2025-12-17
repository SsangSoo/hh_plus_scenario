package kr.hhplus.be.server.domain.order.controller.request;

import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidOrderProductRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "productId는 필수여야 한다.",
                        new OrderProductRequest(null, 1L),
                        "productId",
                        "상품 Id는 필수입니다."
                ),
                Arguments.of(
                        "productId는 0보다 커야한다.(0 X)",
                        new OrderProductRequest(0L, 1L),
                        "productId",
                        "유효하지 않은 값입니다. 상품 Id를 확인해주세요."
                ),
                Arguments.of(
                        "productId는 0보다 커야한다.(음수 X)",
                        new OrderProductRequest(-1L, 1L),
                        "productId",
                        "유효하지 않은 값입니다. 상품 Id를 확인해주세요."
                ),
                Arguments.of(
                        "주문수량은 필수여야 한다.",
                        new OrderProductRequest(1L, null),
                        "quantity",
                        "주문 수량은 필수입니다."
                ),
                Arguments.of(
                        "주문수량은 0보다 커야한다.(0 X)",
                        new OrderProductRequest(1L, 0L),
                        "quantity",
                        "주문 수량은 0보다 커야합니다. 확인해주세요"
                ),
                Arguments.of(
                        "주문수량은 0보다 커야한다.(0 X)",

                        new OrderProductRequest(1L, -1L),
                        "quantity",
                        "주문 수량은 0보다 커야합니다. 확인해주세요"
                )
        );
    }
}
