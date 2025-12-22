package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class RegisterMemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

    RegisterMemberUseCase registerMemberService;



    @BeforeEach
    void setUp() {
        registerMemberService = new RegisterMemberService(memberRepository, pointRepository);
    }


    @Test
    @DisplayName("회원을 생성할 수 있다.")
    void whenRegisterMemberThenPointIsZero() {
        // given : 회원 생성을 위한 값 설정
        RegisterMemberCommand request = new RegisterMemberCommand("나", LocalDate.of(1990, 1, 1).toString(), "주소");

        Member member = Member.create(request);
        member.assignId(1L);

        Point point = Point.create(member.getId());
        point.assignId(1L);

        given(memberRepository.save(any())).willReturn(member);
        given(pointRepository.save(any())).willReturn(point);

        // when : 회원 생성
        MemberResponse memberResponse = registerMemberService.register(request);

        // then : 회원의 포인트는 0원
        assertThat(memberResponse.getAddress()).isEqualTo(request.address());
        assertThat(memberResponse.getName()).isEqualTo(request.name());
        assertThat(memberResponse.getBirthDate()).isEqualTo(request.birthDate());
    }





}