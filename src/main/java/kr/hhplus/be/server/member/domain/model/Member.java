package kr.hhplus.be.server.member.domain.model;

import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private String name;
    private String birthDate;
    private String address;

    private Member() {}

    private Member(Long id, String name, String birthDate, String address) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }

    public static Member of(Long id, String name, String birthDate, String address) {
        return new Member(id, name, birthDate, address);
    }


    public static Member create(RegisterMemberCommand registerRequest) {
        Member member = new Member();
        member.name = registerRequest.name();
        member.birthDate = registerRequest.birthDate();
        member.address = registerRequest.address();
        return member;
    }


    public void assignId(long id) {
        this.id = id;
    }
}
