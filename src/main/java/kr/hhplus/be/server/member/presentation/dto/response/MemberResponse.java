package kr.hhplus.be.server.member.presentation.dto.response;

import kr.hhplus.be.server.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long id;
    private String name;
    private String birthDate;
    private String address;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .address(member.getAddress())
                .build();
    }

    public static MemberResponse of(Long id, String name, String birthDate, String address) {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .address(address)
                .build();
    }

    @Builder
    private MemberResponse(Long id, String name, String birthDate, String address) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }

}

