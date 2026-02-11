package kr.hhplus.be.server.couponhistory.application.service;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.couponhistory.infrastructure.persistence.CouponHistoryJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RegisterCouponHistoryServiceTest extends SpringBootTestSupport {


    @Autowired
    private CouponHistoryJpaRepository couponHistoryJpaRepository;

    @AfterEach
    void tearDown() {
        couponHistoryJpaRepository.deleteAllInBatch();
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @BeforeEach
    void setUp() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Test
    @DisplayName("redis로 저장된 회원의 쿠폰 발행 내역은 RDB에 저장된 후, 지워져야 한다.")
    void whenCouponHistoryRegisterThenDeleteInRedisTest() {
        // given
        Long couponId = 1L;
        Long memberId = 1L;
        redisUtil.set("coupon:" + couponId + ":member:" + memberId, "1");

        // when
        registerCouponHistoryUseCase.register(couponId, memberId);

        // then
        String result = redisUtil.get("coupon:" + couponId + ":member:" + memberId);
        Assertions.assertThat(result).isNull();
    }

}