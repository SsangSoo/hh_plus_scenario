package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RetrievePointServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

//    @Mock
//    PointHistoryRepository pointHistoryRepository;

    RetrievePointUseCase retrievePointUseCase;


    @BeforeEach
    void setUp() {
        retrievePointUseCase = new RetrievePointService(memberRepository, pointRepository);
    }


    @Test
    @DisplayName("회원Id로 회원의 포인트를 조회할 수 있다.")
    void retrievePointTest() {
        // given : 포인트 조회를 위한 값 설정
        Long memberId = 1L;
        RegisterMemberCommand registerMemberCommand = new RegisterMemberCommand("나", LocalDate.of(1990, 1, 1).toString(), "주소");
        Member member = Member.create(registerMemberCommand);
        member.assignId(memberId);

        Long pointId = 1L;
        Point point = Point.create(memberId);
        point.assignId(pointId);

        given(memberRepository.retrieve(any())).willReturn(member);
        given(pointRepository.findByMemberId(any())).willReturn(point);

        // when : 포인트 조회
        PointResponse response = retrievePointUseCase.retrieve(pointId);

        // then : 회원 정보 확인
        assertThat(response.getId()).isEqualTo(point.getId());
        assertThat(response.getMemberId()).isEqualTo(point.getMemberId());
        assertThat(response.getPoint()).isEqualTo(point.getPoint());
    }

}