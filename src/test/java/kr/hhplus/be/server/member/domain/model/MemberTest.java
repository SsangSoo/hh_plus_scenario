package kr.hhplus.be.server.member.domain.model;

import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("회원 생성 테스트")
    void createMemberTest() {
        Member member = Member.create(new RegisterMemberCommand("이름", LocalDate.now().toString(), "주소"));

        assertThat(member.getId()).isNull();
        assertThat(member.getName()).isEqualTo("이름");
    }

    @Test
    @DisplayName("of 으로 테스트")
    void ofTest() {
        Member member = Member.of(1L, "이름", LocalDate.now().toString(), "주소");

        assertThat(member.getId()).isNotNull();
        assertThat(member.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("id를 메서드로 추가할 수 있다.")
    void assignIdTest() {
        Member member = Member.create(new RegisterMemberCommand("이름", LocalDate.now().toString(), "주소"));

        member.assignId(1L);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getId()).isEqualTo(1L);
    }
}