package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.domain.event.CouponIssueEvent;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class IssueCouponServiceRedisTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    CouponRepository couponRepository;

    @Mock
    RedisUtil redisUtil;

    @Mock
    ApplicationEventPublisher eventPublisher;

    IssueCouponService issueCouponService;

    @BeforeEach
    void setUp() {
        issueCouponService = new IssueCouponService(
                memberRepository,
                couponRepository,
                redisUtil,
                eventPublisher
        );
    }

    @Test
    @DisplayName("쿠폰 발행 성공 시, Redis에 중복 방지 키 저장, 수량 차감, 이벤트 발행이 수행된다")
    void 쿠폰_발행_성공_테스트() {
        // given
        Long memberId = 1L;
        Long couponId = 2L;

        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(memberId);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(couponId);

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(couponRepository.retrieve(couponId)).willReturn(coupon);
        given(redisUtil.setIfAbsent(anyString(), eq("1"), any(Duration.class))).willReturn(true);
        given(redisUtil.decrement(anyString())).willReturn(9L);
        given(redisUtil.get(anyString())).willReturn("9");

        IssueCouponServiceRequest request = new IssueCouponServiceRequest(couponId, memberId);

        // when
        issueCouponService.issue(request);

        // then
        then(redisUtil).should(times(1)).setIfAbsent(
                eq("coupon:" + couponId + ":member:" + memberId),
                eq("1"),
                any(Duration.class)
        );
        then(redisUtil).should(times(1)).decrement("coupon:" + couponId);
        then(eventPublisher).should(times(1)).publishEvent(any(CouponIssueEvent.class));
    }

    @Test
    @DisplayName("이미 발행된 쿠폰이면 ALREADY_HAVE_THIS_COUPON 예외가 발생한다")
    void 이미_발행된_쿠폰_예외() {
        // given
        Long memberId = 1L;
        Long couponId = 2L;

        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(memberId);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(couponId);

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(couponRepository.retrieve(couponId)).willReturn(coupon);
        given(redisUtil.setIfAbsent(anyString(), eq("1"), any(Duration.class))).willReturn(false);

        IssueCouponServiceRequest request = new IssueCouponServiceRequest(couponId, memberId);

        // when & then
        assertThatThrownBy(() -> issueCouponService.issue(request))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.ALREADY_HAVE_THIS_COUPON.getMessage());

        then(redisUtil).should(never()).decrement(anyString());
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @Test
    @DisplayName("쿠폰 수량이 부족하면 롤백(increment) 후 예외가 발생한다")
    void 수량_부족시_롤백_후_예외() {
        // given
        Long memberId = 1L;
        Long couponId = 2L;

        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(memberId);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(couponId);

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(couponRepository.retrieve(couponId)).willReturn(coupon);
        given(redisUtil.setIfAbsent(anyString(), eq("1"), any(Duration.class))).willReturn(true);
        given(redisUtil.decrement(anyString())).willReturn(-1L);
        given(redisUtil.get(anyString())).willReturn("-1");

        IssueCouponServiceRequest request = new IssueCouponServiceRequest(couponId, memberId);

        // when & then
        assertThatThrownBy(() -> issueCouponService.issue(request))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER.getMessage());

        then(redisUtil).should(times(1)).increment("coupon:" + couponId);
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @Test
    @DisplayName("쿠폰 발행 성공 시, CouponIssueEvent에 올바른 couponId와 memberId가 포함된다")
    void 이벤트_데이터_검증() {
        // given
        Long memberId = 100L;
        Long couponId = 200L;

        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(memberId);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(couponId);

        given(memberRepository.retrieve(memberId)).willReturn(member);
        given(couponRepository.retrieve(couponId)).willReturn(coupon);
        given(redisUtil.setIfAbsent(anyString(), eq("1"), any(Duration.class))).willReturn(true);
        given(redisUtil.decrement(anyString())).willReturn(9L);
        given(redisUtil.get(anyString())).willReturn("9");

        IssueCouponServiceRequest request = new IssueCouponServiceRequest(couponId, memberId);

        // when
        issueCouponService.issue(request);

        // then
        ArgumentCaptor<CouponIssueEvent> captor = ArgumentCaptor.forClass(CouponIssueEvent.class);
        then(eventPublisher).should().publishEvent(captor.capture());

        CouponIssueEvent capturedEvent = captor.getValue();
        assertThat(capturedEvent.couponId()).isEqualTo(couponId);
        assertThat(capturedEvent.memberId()).isEqualTo(memberId);
    }
}
