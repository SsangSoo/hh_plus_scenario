package kr.hhplus.be.server.member.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;

import java.time.LocalDate;

public record RegisterMemberRequest(

        @NotNull(message = "이름은 필수입니다.")
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        LocalDate birthDate,
        String address
//        String password,
//        String passwordConfirm
) {

    public RegisterMemberCommand toServiceRequest() {
        return new RegisterMemberCommand(name, birthDate.toString(), address);
    }


}
