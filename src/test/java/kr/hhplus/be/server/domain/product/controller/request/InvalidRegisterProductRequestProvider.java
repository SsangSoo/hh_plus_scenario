package kr.hhplus.be.server.domain.product.controller.request;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidRegisterProductRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "상품 등록시 상품 이름은 필수여야 한다.",
                        new RegisterProductRequest(null, 5000L),
                        "productName",
                        "상품 이름은 필수입니다."
                ),
                Arguments.of(
                        "상품 등록시 상품 이름에 공백은 들어갈 수 없다",
                        new RegisterProductRequest(" ", 5000L),
                        "productName",
                        "상품 이름은 필수입니다."
                ),
                Arguments.of(
                        "상품 등록시 상품 이름에 빈 칸은 들어갈 수 없다",
                        new RegisterProductRequest("", 5000L),
                        "productName",
                        "상품 이름은 필수입니다."
                ),
                Arguments.of(
                        "상품 등록시 가격은 필수여야 한다.",
                        new RegisterProductRequest("product", null),
                        "price",
                        "상품 등록시 가격은 필수입니다."
                ),
                Arguments.of(
                        "상품 등록시 가격에 음수는 들어올 수 없다.",
                        new RegisterProductRequest("product", -1L),
                        "price",
                        "가격은 0이상 이어야 합니다."
                )
        );
    }
}
