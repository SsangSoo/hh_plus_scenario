package kr.hhplus.be.server.order.presentation.request;

import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

public class InvalidOrderRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "memberId는 필수여야 한다.",
                        new OrderRequest(null, List.of(new OrderProductRequest(1L, 1L))),
                        "memberId",
                        "사용자 Id는 필수입니다."
                ),
                Arguments.of(
                        "memberId는 0보다 커야한다.",
                        new OrderRequest(0L, List.of(new OrderProductRequest(1L, 1L))),
                        "memberId",
                        "유효하지 않은 값입니다. 사용자 Id를 확인해주세요."
                ),
                Arguments.of(
                        "memberId는 0보다 커야한다.",
                        new OrderRequest(-1L, List.of(new OrderProductRequest(1L, 1L))),
                        "memberId",
                        "유효하지 않은 값입니다. 사용자 Id를 확인해주세요."
                )
        );
    }
}
