package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.dto.request.RegisterCouponServiceRequest;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class CouponIntegrationTest extends SpringBootTestSupport {

    @BeforeEach
    void setUp() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @AfterEach
    void tearDown() {
        paymentJpaRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        couponHistoryJpaRepository.deleteAllInBatch();
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @Test
    @DisplayName("쿠폰 발행 후, Redis에서 개수 확인 테스트")
    void couponIssueAfterVerifyRedisAmount() {
        // given
        MemberResponse registeredMember = registerMemberUseCase.register(new RegisterMemberCommand("이름", "199010101", "주소"));
        CouponResponse registeredCoupon = registerCouponUseCase.register(new RegisterCouponServiceRequest("123456789012345", LocalDate.now().plusDays(1L), 2, 10));

        // Redis 초기 수량 확인
        String initialAmount = stringRedisTemplate.opsForValue().get("coupon:" + registeredCoupon.getCouponId());
        assertThat(Integer.parseInt(initialAmount)).isEqualTo(2);

        // when
        issueCouponUseCase.issue(new IssueCouponServiceRequest(registeredCoupon.getCouponId(), registeredMember.getId()));

        // then - Redis에서 수량 확인
        String redisAmount = stringRedisTemplate.opsForValue().get("coupon:" + registeredCoupon.getCouponId());
        assertThat(Integer.parseInt(redisAmount)).isEqualTo(1);
    }

    @Test
    @DisplayName("쿠폰 발행은 선착순이다.")
    void couponIssuedFirstComeFirstServedTest() throws InterruptedException {
        // given
        int couponAmount = 1000;
        CouponResponse couponResponse = registerCouponUseCase.register(new RegisterCouponServiceRequest("10% 를 할인해주는 쿠폰", LocalDate.now().plusDays(1L), couponAmount, 10));
        Long couponId = couponResponse.getCouponId();

        List<MemberJpaEntity> memberJpaList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            memberJpaList.add(MemberJpaEntity.from(Member.create(new RegisterMemberCommand("이름" + i, "19900101", "베이커가 " + i + "번지"))));
        }

        List<MemberResponse> memberResponseList = memberJpaRepository.saveAll(memberJpaList)
                .stream()
                .map(MemberJpaEntity::toDomain)
                .map(MemberResponse::from)
                .toList();

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            long memberId = memberResponseList.get(i).getId();
            executorService.submit(() -> {
                try {
                    issueCouponUseCase.issue(new IssueCouponServiceRequest(couponId, memberId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1000);
        assertThat(failCount.get()).isEqualTo(0);

        // Redis에서 쿠폰 수량 확인 (선착순 처리는 Redis에서 수행)
        String redisAmount = stringRedisTemplate.opsForValue().get("coupon:" + couponId);
        assertThat(Integer.parseInt(redisAmount)).isEqualTo(0);
    }
}
