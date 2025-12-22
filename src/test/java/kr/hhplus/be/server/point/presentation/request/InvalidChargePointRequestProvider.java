package kr.hhplus.be.server.point.presentation.request;

import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidChargePointRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                        "포인트 충전시 사용자 Id는 필수여야 한다.",
                        new ChargePointRequest(null, 5000L),
                        "memberId",
                        "사용자 Id는 필수입니다."
                ),
                Arguments.of(
                        "사용자 Id는 당연히 0보다 커야한다. (0 X)",
                        new ChargePointRequest(0L, 5000L),
                        "memberId",
                        "회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요"
                ),
                Arguments.of(
                        "사용자 Id는 당연히 0보다 커야한다. (음수 X)",
                        new ChargePointRequest(-1L, 5000L),
                        "memberId",
                        "회원 Id가 올바르지 않은 값입니다. 다시 확인해주세요"
                ),
                Arguments.of(
                        "포인트 충전시 충전 금액은 필수입니다.",
                        new ChargePointRequest(1L, null),
                        "chargePoint",
                        "포인트 충전시 충전 금액은 필수입니다."
                ),
                Arguments.of(
                        "포인트 충전시 충전 금액은 0보다 커야한다.(0 X)",
                        new ChargePointRequest(1L, 0L),
                        "chargePoint",
                        "충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요"
                ),
                Arguments.of(
                        "포인트 충전시 충전 금액은 0보다 커야한다.(음수 X)",
                        new ChargePointRequest(1L, -1L),
                        "chargePoint",
                        "충전 포인트 금액이 유효하지 않습니다. 다시 확인해주세요"
                )
        );
    }
}
