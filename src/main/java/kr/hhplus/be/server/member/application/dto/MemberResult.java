package kr.hhplus.be.server.member.application.dto;

import kr.hhplus.be.server.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResult {

    private Long id;
    private String name;
    private String birthDate;
    private String address;

    public static MemberResult from(Member member) {
        return MemberResult.builder()
                .id(member.getId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .address(member.getAddress())
                .build();
    }

    public static MemberResult of(Long id, String name, String birthDate, String address) {
        return MemberResult.builder()
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .address(address)
                .build();
    }

    @Builder
    private MemberResult(Long id, String name, String birthDate, String address) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }

}
