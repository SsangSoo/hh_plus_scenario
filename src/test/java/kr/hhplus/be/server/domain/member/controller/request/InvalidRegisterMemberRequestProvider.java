package kr.hhplus.be.server.domain.member.controller.request;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDate;
import java.util.stream.Stream;

public class InvalidRegisterMemberRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        "회원 생성시 이름은 필수다.",
                        new RegisterMemberRequest(null, LocalDate.now(), "주소"),
                        "name",
                        "이름은 필수입니다."
                ));
    }
}
