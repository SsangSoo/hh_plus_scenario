package kr.hhplus.be.server.domain.member.entity;

import kr.hhplus.be.server.domain.member.service.request.RegisterMemberServiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("회원은 이름과 나이, 주소로 생성할 수 있다. 삭제 여부는 기본값은 false다. 생성일자와 수정일자가 동일해야 한다.")
    void registerMemberTest() {
        // given : 멤버 요청 값 설정
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";

        // when : 멤버 생성
        Member member = Member.register(new RegisterMemberServiceRequest(name, birthDate, address));

        // then : 멤버 생성 검증
        assertThat(member).isNotNull();
        assertThat(member.getId()).isNull();
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getBirthDate()).isEqualTo(birthDate);
        assertThat(member.getAddress()).isEqualTo(address);
        assertThat(member.getDeleted()).isFalse();
        assertThat(member.getCreatedDate()).isEqualTo(member.getModifiedDate());
    }


    @Test
    @DisplayName("회원 삭제는 논리적 삭제다")
    void deleteMemberTest() {
        // given : 멤버 생성
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";

        Member member = Member.register(new RegisterMemberServiceRequest(name, birthDate, address));

        // when : 멤버 삭제
        member.delete();

        // then : 멤버 삭제확인 / 단 멤버가 없어지진 않는다(=isNotNull)
        assertThat(member).isNotNull();
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getBirthDate()).isEqualTo(birthDate);
        assertThat(member.getAddress()).isEqualTo(address);

        assertThat(member.getDeleted()).isTrue();
    }



}