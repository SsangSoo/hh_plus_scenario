package kr.hhplus.be.server.domain.point.service;

import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.request.ChargePoint;
import kr.hhplus.be.server.domain.point.service.response.PointResponse;
import kr.hhplus.be.server.domain.pointhistory.entity.PointHistory;
import kr.hhplus.be.server.domain.pointhistory.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    PointRepository pointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

    PointService pointService;


    @BeforeEach
    void setUp() {
        pointService = new PointService(pointRepository, pointHistoryRepository);
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다.")
    void chargePointTest() {
        // given : 포인트 충전을 위한 값 설정
        Long memberId = 1L;
        Long pointId = 1L;
        Long chargePoint = 3000L;

        Point point = Point.register(memberId);
        Util.setId(point, pointId);

        given(pointRepository.findMemberIdByMemberId(memberId)).willReturn(Optional.of(memberId));
        given(pointRepository.findPointByMemberIdAndDeletedFalse(memberId)).willReturn(Optional.of(point));

        // when : 포인트 충전
        PointResponse pointResponse = pointService.charge(new ChargePoint(memberId, chargePoint));

        // then : 충전된 포인트 확인
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(chargePoint);

        then(pointHistoryRepository).should(times(1)).save(any(PointHistory.class));
    }



    @Test
    @DisplayName("회원Id로 회원의 포인트를 조회할 수 있다.")
    void retrievePointTest() {
        // given : 포인트 조회를 위한 값 설정
        Long memberId = 1L;
        Long pointId = 1L;

        Point point = Point.register(memberId);
        Util.setId(point, pointId);

        given(pointRepository.findMemberIdByMemberId(any())).willReturn(Optional.of(memberId));
        given(pointRepository.findPointByMemberIdAndDeletedFalse(any())).willReturn(Optional.of(point));

        // when : 포인트 조회
        PointResponse response = pointService.retrieve(memberId);

        // then : 회원 정보 확인
        assertThat(response.getId()).isEqualTo(point.getId());
        assertThat(response.getMemberId()).isEqualTo(point.getMemberId());
        assertThat(response.getPoint()).isEqualTo(point.getPoint());
    }



}