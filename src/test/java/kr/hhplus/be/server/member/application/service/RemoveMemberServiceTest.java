package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.member.application.usecase.RemoveMemberUseCase;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.infrastructure.persistence.PointJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveMemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

    RemoveMemberUseCase removeMemberService;



    @BeforeEach
    void setUp() {
        removeMemberService = new RemoveMemberService(memberRepository, pointRepository);
    }

    @Test
    @DisplayName("회원을 삭제할 수 있다.")
    void memberRemoveThenPointRemoveTest() {
        // given : 회원 생성 및 포인트 생성
        Long memberId = 1L;
        Long pointId = 1L;

        RegisterMemberCommand request = new RegisterMemberCommand("나", LocalDate.of(1990, 1, 1).toString(), "주소");
        Member member = Member.create(request);
        member.assignId(memberId);

        Point point = Point.create(member.getId());
        point.assignId(pointId);

        given(pointRepository.findByMemberId(any())).willReturn(point);

        // when : 회원 삭제
        removeMemberService.remove(memberId);

        // then : 회원과 포인트 삭제 호출 검증
        then(memberRepository).should(times(1)).remove(any());
        then(pointRepository).should(times(1)).remove(any());

    }




}