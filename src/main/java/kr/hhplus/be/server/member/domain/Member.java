package kr.hhplus.be.server.member.domain;


import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Member {

    private Long id;
    private String name;
    private String birthDate;
    private String address;
    private boolean deleted;

    private Member() {}

    public Member(Long id, String name, String birthDate, String address, boolean deleted) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.deleted = deleted;
    }

    public static Member create(RegisterMemberCommand registerRequest) {
        Member member = new Member();
        member.name = registerRequest.name();
        member.birthDate = registerRequest.birthDate();
        member.address = registerRequest.birthDate();
        member.deleted = false;
        return member;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void delete() {
        this.deleted = true;
    }


}
