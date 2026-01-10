package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.application.dto.request.UsePoint;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.application.usecase.UsePointUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PointConcurrencyTest extends SpringBootTestSupport {

    @Autowired
    private UsePointUseCase usePointUseCase;

    @AfterEach
    void tearDown() {
        pointHistoryJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("시나리오 1: 동시 충전 테스트 - 100개 스레드가 각각 1000 포인트씩 충전")
    void concurrentChargeTest() throws InterruptedException {
        // given: 회원 1명, 초기 포인트 0
        MemberResponse member = registerMemberUseCase.register(
                new RegisterMemberCommand("테스트회원", LocalDate.of(1990, 1, 1).toString(), "테스트주소")
        );

        int threadCount = 100;
        Long chargeAmount = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 100개 스레드가 각각 1000 포인트씩 동시 충전
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    chargePointUseCase.charge(new ChargePoint(member.getId(), chargeAmount));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then: 최종 포인트는 100,000이어야 함
        Point finalPoint = pointRepository.findByMemberId(member.getId());
        assertThat(finalPoint.getPoint()).isEqualTo(100000L);
    }

    @Test
    @DisplayName("시나리오 2: 동시 사용 테스트 - 100개 스레드가 각각 500 포인트씩 사용")
    void concurrentUseTest() throws InterruptedException {
        // given: 회원 1명, 초기 포인트 100,000
        MemberResponse member = registerMemberUseCase.register(
                new RegisterMemberCommand("테스트회원", LocalDate.of(1990, 1, 1).toString(), "테스트주소")
        );
        chargePointUseCase.charge(new ChargePoint(member.getId(), 100000L));

        int threadCount = 100;
        Long useAmount = 500L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 100개 스레드가 각각 500 포인트씩 동시 사용
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    usePointUseCase.use(new UsePoint(member.getId(), useAmount));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then: 최종 포인트는 50,000이어야 함
        Point finalPoint = pointRepository.findByMemberId(member.getId());
        assertThat(finalPoint.getPoint()).isEqualTo(50000L);
    }

    @Test
    @DisplayName("시나리오 3: 충전/사용 혼합 테스트 - 50개는 1000 충전, 50개는 500 사용")
    void concurrentChargeAndUseTest() throws InterruptedException {
        // given: 회원 1명, 초기 포인트 50,000
        MemberResponse member = registerMemberUseCase.register(
                new RegisterMemberCommand("테스트회원", LocalDate.of(1990, 1, 1).toString(), "테스트주소")
        );
        chargePointUseCase.charge(new ChargePoint(member.getId(), 50000L));

        int chargeThreadCount = 50;
        int useThreadCount = 50;
        int totalThreadCount = chargeThreadCount + useThreadCount;
        Long chargeAmount = 1000L;
        Long useAmount = 500L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalThreadCount);

        // when: 50개 스레드는 1000 포인트 충전, 50개 스레드는 500 포인트 사용
        for (int i = 0; i < chargeThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    chargePointUseCase.charge(new ChargePoint(member.getId(), chargeAmount));
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < useThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    usePointUseCase.use(new UsePoint(member.getId(), useAmount));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then: 최종 포인트는 75,000이어야 함 (50,000 + 50*1,000 - 50*500 = 75,000)
        Point finalPoint = pointRepository.findByMemberId(member.getId());
        assertThat(finalPoint.getPoint()).isEqualTo(75000L);
    }

    @Test
    @DisplayName("시나리오 4: 포인트 부족 시나리오 - 10개 스레드가 각각 1000씩 사용, 5개만 성공")
    void insufficientPointTest() throws InterruptedException {
        // given: 회원 1명, 초기 포인트 5,000
        MemberResponse member = registerMemberUseCase.register(
                new RegisterMemberCommand("테스트회원", LocalDate.of(1990, 1, 1).toString(), "테스트주소")
        );
        chargePointUseCase.charge(new ChargePoint(member.getId(), 5000L));

        int threadCount = 10;
        Long useAmount = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 10개 스레드가 각각 1,000 포인트씩 동시 사용
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    usePointUseCase.use(new UsePoint(member.getId(), useAmount));
                    successCount.incrementAndGet();
                } catch (BusinessLogicRuntimeException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then: 5개만 성공, 5개는 실패
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(5);

        Point finalPoint = pointRepository.findByMemberId(member.getId());
        assertThat(finalPoint.getPoint()).isEqualTo(0L);
    }
}
