package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import kr.hhplus.be.server.point.service.request.ChargePoint;
import kr.hhplus.be.server.point.service.response.PointResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RegisterMemberServiceIntegratedTest extends SpringBootTestSupport {


    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        pointRepository.deleteAllInBatch();
        pointHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원을 생성하고, 포인트를 충전하고, 충전한 포인트가 잘 조회되는지 테스트")
    void pointChargeAndRetrieveTest() {
        // given : 회원 생성 및 포인트 충전
        String name = "내이름";
        String birthDate = LocalDate.of(1990, 1, 1).toString();
        String address = "주소";
        Long requestChargePoint = 1000L;

        MemberResult memberResponse = memberService.register(new RegisterMemberCommand(name, birthDate, address));

        PointResponse pointResponse = pointService.retrieve(memberResponse.getId());
        assertThat(pointResponse.getPoint()).isZero();

        pointService.charge(new ChargePoint(memberResponse.getId(), requestChargePoint));

        // when  : 충전된 포인트 조회
        pointResponse = pointService.retrieve(memberResponse.getId());

        // then  : 포인트가 잘 충전되었는지 확인
        assertThat(pointResponse.getPoint()).isEqualTo(requestChargePoint);
    }


}