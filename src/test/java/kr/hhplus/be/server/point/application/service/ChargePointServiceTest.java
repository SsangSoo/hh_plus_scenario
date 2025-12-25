package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ChargePointServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

    ChargePointUseCase chargePointUseCase;


    @BeforeEach
    void setUp() {
        chargePointUseCase = new ChargePointService(memberRepository, pointRepository, pointHistoryRepository);
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다.")
    void chargePointTest() {
        // given : 포인트 충전을 위한 값 설정
        Long memberId = 1L;
        Member member = Member.create(new RegisterMemberCommand("나", LocalDate.of(1990, 1, 1).toString(), "주소"));
        member.assignId(memberId);

        Long pointId = 1L;
        Long chargePoint = 3000L;

        Point point = Point.create(memberId);
        point.assignId(pointId);

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(pointRepository.findByMemberId(memberId)).willReturn(point);

        // when : 포인트 충전
        PointResponse pointResponse = chargePointUseCase.charge(new ChargePoint(memberId, chargePoint));

        // then : 충전된 포인트 확인
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(chargePoint);
        then(pointHistoryRepository).should(times(1)).save(any(PointHistory.class));
    }


}