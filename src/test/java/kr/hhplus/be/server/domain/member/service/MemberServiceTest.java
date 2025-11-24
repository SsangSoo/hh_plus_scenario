package kr.hhplus.be.server.domain.member.service;

import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.domain.member.entity.Member;
import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.member.service.request.RegisterMemberServiceRequest;
import kr.hhplus.be.server.domain.member.service.response.MemberResponse;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

    MemberService memberService;


    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, pointRepository);
    }


    @Test
    @DisplayName("회원을 생성할 수 있다.")
    void whenRegisterMemberThenPointIsZero() {
        // given : 회원 생성을 위한 값 설정
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";
        Long memberId = 1L;
        Long pointId = 1L;

        RegisterMemberServiceRequest request = new RegisterMemberServiceRequest(name, birthDate, address);
        Member member = Member.register(request);
        Util.setId(member, memberId);

        Point point = Point.register(member.getId());
        Util.setId(point, pointId);

        given(memberRepository.save(any())).willReturn(member);
        given(pointRepository.save(any())).willReturn(point);

        // when : 회원 생성
        MemberResponse memberResponse = memberService.register(request);

        // then : 회원의 포인트는 0원
        assertThat(memberResponse.getAddress()).isEqualTo(address);
        assertThat(memberResponse.getName()).isEqualTo(name);
        assertThat(memberResponse.getBirthDate()).isEqualTo(birthDate);
    }



    @Test
    @DisplayName("회원을 삭제할 수 있다.")
    void memberRemoveThenPointRemoveTest() {
        // given : 회원 생성 및 포인트 생성
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";
        Long memberId = 1L;
        Long pointId = 1L;

        RegisterMemberServiceRequest request = new RegisterMemberServiceRequest(name, birthDate, address);
        Member member = Member.register(request);
        Util.setId(member, memberId);

        Point point = Point.register(member.getId());
        Util.setId(point, pointId);

        given(memberRepository.findMemberByIdAndDeletedFalse(any())).willReturn(Optional.of(member));
        given(pointRepository.findPointByMemberIdAndDeletedFalse(any())).willReturn(Optional.of(point));

        // when : 회원 삭제
        memberService.remove(memberId);

        // then : 회원과 포인트 삭제 여부 검증
        assertThat(member.getDeleted()).isTrue();
    }


    @Test
    @DisplayName("회원 정보를 조회한다.")
    void retrieveTest() {
        // given : 회원 조회를 위한 값 설정
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";
        Long memberId = 1L;
        Long pointId = 1L;

        RegisterMemberServiceRequest request = new RegisterMemberServiceRequest(name, birthDate, address);
        Member member = Member.register(request);
        Util.setId(member, memberId);

        Point point = Point.register(member.getId());
        Util.setId(point, pointId);

        given(memberRepository.findMemberByIdAndDeletedFalse(any())).willReturn(Optional.of(member));

        // when : 회원 조회
        MemberResponse response = memberService.retrieve(memberId);

        // then : 회원 정보 확인
        assertThat(response.getName()).isEqualTo(name);
        assertThat(response.getBirthDate()).isEqualTo(birthDate);
        assertThat(response.getAddress()).isEqualTo(address);
    }


    @Test
    @DisplayName("삭제된 회원은 조회되지 않는다.")
    void cannotFoundDeletedMemberTest() {
        // given : 회원 조회 및 삭제를 위한 Mocking
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";
        Long memberId = 1L;
        Long pointId = 1L;

        RegisterMemberServiceRequest request = new RegisterMemberServiceRequest(name, birthDate, address);
        Member member = Member.register(request);
        Util.setId(member, memberId);

        Point point = Point.register(member.getId());
        Util.setId(point, pointId);

        given(memberRepository.findMemberByIdAndDeletedFalse(any())).willReturn(Optional.of(member));
        given(pointRepository.findPointByMemberIdAndDeletedFalse(any())).willReturn(Optional.of(point));

        // 회원 삭제
        memberService.remove(memberId);

        given(memberRepository.findMemberByIdAndDeletedFalse(any())).willReturn(Optional.empty());

        // when // then  회원 조회시 예외 발생: 회원 정보 확인
        assertThatThrownBy(() -> memberService.retrieve(memberId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }


}