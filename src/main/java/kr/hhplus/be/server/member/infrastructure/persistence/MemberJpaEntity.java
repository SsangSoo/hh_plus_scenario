package kr.hhplus.be.server.member.infrastructure.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.base.BaseEntity;
import kr.hhplus.be.server.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "address")
    private String address;


    public static MemberJpaEntity from(Member member) {
        MemberJpaEntity memberJpaEntity = new MemberJpaEntity();

        memberJpaEntity.name = member.getName();
        memberJpaEntity.birthDate = member.getBirthDate();
        memberJpaEntity.address = member.getAddress();

        memberJpaEntity.createdDate = LocalDateTime.now();
        memberJpaEntity.modifiedDate = memberJpaEntity.createdDate;
        memberJpaEntity.removed = false;

        return memberJpaEntity;
    }


    public Member toDomain() {
        return Member.of(
                this.id,
                this.name,
                this.birthDate,
                this.address
        );
    }

}
