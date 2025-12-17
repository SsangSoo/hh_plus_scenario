package kr.hhplus.be.server.domain.stock.controller.request;

import kr.hhplus.be.server.stock.controller.request.AddStockRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidAddStockRequestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "상품 재고 충전시 상품 Id는 필수여야 한다.",
                        new AddStockRequest(null, 1000L),
                        "productId",
                        "상품 Id는 필수입니다."
                ),
                Arguments.of(
                        "상품 재고 충전시 상품 Id는 당연히 0보다 커야한다. (0 X)",
                        new AddStockRequest(0L, 5000L),
                        "productId",
                        "상품 Id가 유효하지 않습니다"
                ),
                Arguments.of(
                        "상품 재고 충전시 상품 Id는 당연히 0보다 커야한다. (음수 X)",
                        new AddStockRequest(-1L, 5000L),
                        "productId",
                        "상품 Id가 유효하지 않습니다"
                ),
                Arguments.of(
                        "상품 재고 충전시 재고 충전값은 필수다.",
                        new AddStockRequest(1L, null),
                        "addStock",
                        "재고 충전시 재고 충전 값은 필수입니다."
                ),
                Arguments.of(
                        "상품 재고 충전시 재고 충전값은 0보다 커야한다.(0 X)",
                        new AddStockRequest(1L, 0L),
                        "addStock",
                        "재고 충전 값은 0보다 커야합니다."
                ),
                Arguments.of(
                        "상품 재고 충전시 재고 충전값은 0보다 커야한다.(음수 X)",
                        new AddStockRequest(1L, -1L),
                        "addStock",
                        "재고 충전 값은 0보다 커야합니다."
                )
        );
    }
}
