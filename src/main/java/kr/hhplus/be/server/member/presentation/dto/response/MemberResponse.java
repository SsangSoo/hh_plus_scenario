package kr.hhplus.be.server.member.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private Long id;
    private String name;
    private String birthDate;
    private String address;

    public static MemberResponse from(MemberResult memberResult) {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.id = memberResult.getId();
        memberResponse.name = memberResult.getName();
        memberResponse.birthDate = memberResult.getBirthDate();
        memberResponse.address = memberResult.getAddress();
        return  memberResponse;
    }

}
