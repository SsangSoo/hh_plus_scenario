package kr.hhplus.be.server.domain.member.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.base.BaseEntity;
import kr.hhplus.be.server.domain.member.service.request.RegisterMemberServiceRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "address")
    private String address;


    @Column(name = "deleted")
    private Boolean deleted;


    public static Member register(RegisterMemberServiceRequest request) {
        return register(request.name(), request.birthDate(), request.address());
    }

    private static Member register(String name, String birthDate, String address) {
        Member member = new Member();

        member.name = name;
        member.birthDate = birthDate;
        member.address = address;

        member.deleted = false;

        return member;
    }

    public void delete() {
        this.deleted = true;
    }

}
