package kr.hhplus.be.server.domain.pointhistory.entity;

import kr.hhplus.be.server.domain.pointhistory.service.request.RegisterPointHistoryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PointHistoryTest {

    @Test
    @DisplayName("포인트 내역 생성테스트")
    void registerPointHistoryTest() {
        // given : 포인트 내역 생성을 위한 값 설정
        Long memberId = 1L;
        Long pointId = 1L;
        Long pointAmount = 1000L;
        LocalDateTime createdDate = LocalDateTime.now();
        Long totalPoint = 1000L;

        // when : 포인트 내역 생성
        RegisterPointHistoryRequest request = new RegisterPointHistoryRequest(memberId, pointId, pointAmount, createdDate, totalPoint);

        PointHistory pointHistory1 = PointHistory.register(request, State.CHARGE);
        PointHistory pointHistory2 = PointHistory.register(request, State.USE);
        PointHistory pointHistory3 = PointHistory.register(request, State.REFUND);

        // then : 포인트 내역 생성 확인(상태)
        assertThat(pointHistory1).isNotNull();
        assertThat(pointHistory1.getState()).isEqualTo(State.CHARGE);
        assertThat(pointHistory2).isNotNull();
        assertThat(pointHistory2.getState()).isEqualTo(State.USE);
        assertThat(pointHistory3).isNotNull();
        assertThat(pointHistory3.getState()).isEqualTo(State.REFUND);
    }

}