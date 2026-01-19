package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.service.charge.ChargePointService;
import kr.hhplus.be.server.point.application.service.charge.ChargePointTransactionService;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChargePointServiceTest {

    @Mock
    RedissonClient redissonClient;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ChargePointTransactionService transactionService;

    ChargePointUseCase chargePointUseCase;


    @BeforeEach
    void setUp() throws InterruptedException {
        // Mock RLock 설정
        RLock mockLock = mock(RLock.class);
        given(redissonClient.getLock(anyString())).willReturn(mockLock);
        given(mockLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
        given(mockLock.isHeldByCurrentThread()).willReturn(true);

        chargePointUseCase = new ChargePointService(redissonClient, memberRepository, transactionService);
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
        ChargePoint chargePointRequest = new ChargePoint(memberId, chargePoint);

        PointResponse expectedResponse = PointResponse.builder()
                .id(pointId)
                .memberId(memberId)
                .point(chargePoint)
                .build();

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(transactionService.chargeInternal(chargePointRequest)).willReturn(expectedResponse);

        // when : 포인트 충전
        PointResponse pointResponse = chargePointUseCase.charge(chargePointRequest);

        // then : 충전된 포인트 확인
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(chargePoint);
        then(transactionService).should(times(1)).chargeInternal(any(ChargePoint.class));
    }


}