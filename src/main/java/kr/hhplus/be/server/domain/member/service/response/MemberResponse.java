package kr.hhplus.be.server.domain.member.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private Long id;
    private String name;
    private String birthDate;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .birthDate(member.getBirthDate())
                .address(member.getAddress())
                .createdDate(member.getCreatedDate().withNano(0))
                .modifiedDate(member.getModifiedDate().withNano(0))
                .build();
    }

    public static MemberResponse of(Long id, String name, String birthDate, String address, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .address(address)
                .createdDate(createdDate.withNano(0))
                .modifiedDate(modifiedDate.withNano(0))
                .build();
    }

    @Builder
    private MemberResponse(Long id, String name, String birthDate, String address, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

}
