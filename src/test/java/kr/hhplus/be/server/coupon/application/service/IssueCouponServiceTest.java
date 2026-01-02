package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IssueCouponServiceTest {


    @Mock
    CouponRepository couponRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    CouponHistoryRepository couponHistoryRepository;


    IssueCouponUseCase issueCouponUseCase;


    @BeforeEach
    void setUp() {
        issueCouponUseCase = new IssueCouponService(couponRepository, memberRepository, couponHistoryRepository);
    }

    @Test
    @DisplayName("쿠폰 발행 로직 테스트")
    void issueCouponUseCaseTest() {
        // given
        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(1L);
        given(memberRepository.retrieve(any())).willReturn(member);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(1L);
        given(couponRepository.retrieve(any())).willReturn(coupon);

        given(couponHistoryRepository.retrieveCouponHistory(anyLong(), anyLong())).willReturn(Optional.empty());

        CouponHistory couponHistory = CouponHistory.create(coupon.getId(), member.getId());
        couponHistory.assignId(1L);
        given(couponHistoryRepository.register(any())).willReturn(couponHistory);

        // when
        IssueCouponResponse issueCouponResponse = issueCouponUseCase.issue(new IssueCouponServiceRequest(member.getId(), coupon.getId()));


        // then
        assertThat(issueCouponResponse).isNotNull();
        assertThat(issueCouponResponse.getCouponHistoryId()).isEqualTo(couponHistory.getId());
        assertThat(issueCouponResponse.getCouponId()).isEqualTo(couponHistory.getCouponId());
        assertThat(issueCouponResponse.getMemberId()).isEqualTo(couponHistory.getMemberId());
        assertThat(issueCouponResponse.getCouponIssuance()).isEqualTo(couponHistory.getCouponIssuance());
        assertThat(issueCouponResponse.getCouponUsed()).isEqualTo(couponHistory.getCouponUsed());
        assertThat(issueCouponResponse.getCouponUsed()).isFalse();
    }


    @Test
    @DisplayName("쿠폰이 없으면, 예외가 발생한다.")
    void ifWithoutCouponWhenIssueCouponThenThrownExceptionTest() {
        // given
        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(1L);
        given(memberRepository.retrieve(any())).willReturn(member);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 0, 10);
        coupon.assignId(1L);
        given(couponRepository.retrieve(any())).willReturn(coupon);

        given(couponHistoryRepository.retrieveCouponHistory(anyLong(), anyLong())).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> issueCouponUseCase.issue(new IssueCouponServiceRequest(member.getId(), coupon.getId())))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER.getMessage());
    }


    // 쿠폰을 멤버가 이미 발행했을 때,
    @Test
    @DisplayName("쿠폰을 이미 발행한 회원이면, 쿠폰을 발행할 수 없다.")
    void ifMemberAlreadyHaveCouponThenNotIssueCouponTest() {
        // given
        Member member = Member.create(new RegisterMemberCommand("이름", "1999-10-11", "주소"));
        member.assignId(1L);
        given(memberRepository.retrieve(any())).willReturn(member);

        Coupon coupon = Coupon.create("abcdefghijkl", LocalDate.now().plusDays(10L), 10, 10);
        coupon.assignId(1L);
        given(couponRepository.retrieve(any())).willReturn(coupon);

        CouponHistory couponHistory = CouponHistory.create(coupon.getId(), member.getId());
        couponHistory.assignId(1L);
        given(couponHistoryRepository.retrieveCouponHistory(anyLong(), anyLong())).willReturn(Optional.of(couponHistory));

        // when // then
        assertThatThrownBy(() -> issueCouponUseCase.issue(new IssueCouponServiceRequest(member.getId(), coupon.getId())))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.ALREADY_HAVE_THIS_COUPON.getMessage());
    }

}
