package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.service.request.ChargePoint;
import kr.hhplus.be.server.point.service.response.PointResponse;
import kr.hhplus.be.server.pointhistory.entity.PointHistory;
import kr.hhplus.be.server.pointhistory.entity.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class PointServiceIntegratedTest extends SpringBootTestSupport {


    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        pointRepository.deleteAllInBatch();
        pointHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원을 생성하면 회원의 포인트는 0포인트다.")
    void whenRegisterMemberThenPointIsZero() {
        // given : 회원 생성을 위한 값 설정
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";

        RegisterMemberCommand request = new RegisterMemberCommand(name, birthDate, address);

        // when : 회원 생성 후 포인트 조회
        MemberResult response = memberService.register(request);
        PointResponse pointResponse = pointService.retrieve(response.getId());

        // then : 회원의 포인트는 0원
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isZero();
    }


    @Test
    @DisplayName("회원을 삭제하면 포인트도 삭제되어야 한다.")
    void memberRemoveThenPointRemoveTest() {
        // given : 회원 생성 및 포인트 생성
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";

        RegisterMemberCommand request = new RegisterMemberCommand(name, birthDate, address);

        // 회원 생성
        MemberResult memberResponse = memberService.register(request);
        PointResponse pointResponse = pointService.retrieve(memberResponse.getId());

        // 회원 삭제
        memberService.remove(memberResponse.getId());

        // then : 회원과 포인트 삭제 여부 검증
        MemberJpaEntity member = memberRepository.findById(memberResponse.getId()).orElseThrow();
        assertThat(member.getDeleted()).isTrue();
        Point point = pointRepository.findById(pointResponse.getId()).orElseThrow();
        assertThat(point.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("포인트를 충전하면 포인트 내역이 저장되어야 한다.")
    void whenPointChargeThenPointHistoryCreateTest() {
        // given : 포인트 충전을 위한 값 설정(회원 생성)
        String name = "나";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";

        RegisterMemberCommand request = new RegisterMemberCommand(name, birthDate, address);

        // 회원 생성
        MemberResult memberResponse = memberService.register(request);


        // when : 포인트 2번 충전
        Long chargePoint = 3000L;
        pointService.charge(new ChargePoint(memberResponse.getId(), chargePoint));
        pointService.charge(new ChargePoint(memberResponse.getId(), chargePoint));

        // then : 포인트 충전 내역 확인
        PointResponse pointResponse = pointService.retrieve(memberResponse.getId());

        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(6000L);

        List<PointHistory> pointHistories = pointHistoryRepository.findAllByPointId(pointResponse.getId());

        assertThat(pointHistories).isNotEmpty();
        assertThat(pointHistories.size()).isEqualTo(2);
        assertThat(pointHistories)
                .extracting("pointId", "pointAmount", "totalPoint", "state")
                .containsExactlyInAnyOrder(
                        tuple(pointResponse.getId(), chargePoint, 3000L, State.CHARGE),
                        tuple(pointResponse.getId(), chargePoint, 6000L, State.CHARGE)
                );
    }

}
