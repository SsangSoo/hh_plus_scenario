package kr.hhplus.be.server.coupon.presentation.dto.request;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDate;
import java.util.stream.Stream;

public class InvalidRegisterCouponRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        "쿠폰 길이는 최소 10자여야 한다.",
                        new RegisterCouponRequest("123456789", LocalDate.now().plusDays(1L), 10, 10),
                        "coupon",
                        "쿠폰 길이는 10~30 자여야 합니다."
                ),
                Arguments.of(
                        "쿠폰 길이는 최소 30자 이하여야 한다.",
                        new RegisterCouponRequest("1234567890123456789012345678901", LocalDate.now().plusDays(1L), 10, 10),
                        "coupon",
                        "쿠폰 길이는 10~30 자여야 합니다."
                ),
                Arguments.of(
                        "쿠폰 값은 필수 값이다.",
                        new RegisterCouponRequest(null, LocalDate.now().plusDays(1L), 10, 10),
                        "coupon",
                        "필수 값입니다."
                ),
                Arguments.of(
                        "쿠폰의 유효기간은 생성하는 날보다 하루 뒤여야한다.(오늘보다 하루 전)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now().minusDays(1L), 10, 10),
                        "expiryDate",
                        "쿠폰의 유효기간은 오늘보다 하루 이상되어야 합니다."
                ),
                Arguments.of(
                        "쿠폰의 유효기간은 생성하는 날보다 하루 뒤여야한다.(오늘)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now(), 10, 10),
                        "expiryDate",
                        "쿠폰의 유효기간은 오늘보다 하루 이상되어야 합니다."
                ),
                Arguments.of(
                        "쿠폰 생성 개수는 1개 이상이어야 한다.(0)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now().plusDays(1L), 0, 10),
                        "amount",
                        "쿠폰의 생성 개수는 1개 이상이어야 한다."
                ),
                Arguments.of(
                        "쿠폰 생성 개수는 1개 이상이어야 한다.(음수)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now().plusDays(1L), -1, 10),
                        "amount",
                        "쿠폰의 생성 개수는 1개 이상이어야 한다."
                ),
                Arguments.of(
                        "할인율은 1개 이상이어야 한다.(0)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now().plusDays(1L), 10, 0),
                        "discountRate",
                        "할인율은 0보다 커야 합니다."
                ),
                Arguments.of(
                        "할인율은 1개 이상이어야 한다.(음수)",
                        new RegisterCouponRequest("123456789012345678901234567890", LocalDate.now().plusDays(1L), 10, -1),
                        "discountRate",
                        "할인율은 0보다 커야 합니다."
                )
        );
    }
}
