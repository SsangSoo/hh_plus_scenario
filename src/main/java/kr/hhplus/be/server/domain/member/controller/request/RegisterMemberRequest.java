package kr.hhplus.be.server.domain.member.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import kr.hhplus.be.server.domain.member.service.request.RegisterMemberServiceRequest;

import java.time.LocalDate;

public record RegisterMemberRequest(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        LocalDate birthDate,

        String address
) {


    public RegisterMemberServiceRequest toServiceRequest() {
        return new RegisterMemberServiceRequest(name, birthDate.toString(), address);
    }
}
