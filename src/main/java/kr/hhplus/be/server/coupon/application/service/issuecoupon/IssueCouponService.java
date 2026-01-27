package kr.hhplus.be.server.coupon.application.service.issuecoupon;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.CouponResponse;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCouponService implements IssueCouponUseCase {

    private final MemberRepository memberRepository;

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    private final RedisUtil redisUtil;

    @Override
    @Transactional
    public IssueCouponResponse issue(IssueCouponServiceRequest serviceRequest) {

        // 멤버 확인
        Member member = memberRepository.retrieve(serviceRequest.memberId());
        Coupon coupon = couponRepository.retrieve(serviceRequest.couponId());

        // 쿠폰 발행(Redis로)
            // 쿠폰 발행 내역 생성(Redis에서 동시에 처리)
        Boolean isCouponIssue = redisUtil.setIfAbsent("coupon:" + serviceRequest.couponId() + ":member:" + serviceRequest.memberId(), "1", Duration.between(LocalDateTime.now(), coupon.getExpiryDate().plusDays(1L).atStartOfDay()));

        // 멤버한테 해당 쿠폰이 이미 있으면 예외
        if (Boolean.FALSE.equals(isCouponIssue)) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.ALREADY_HAVE_THIS_COUPON);
        }

        // 쿠폰 개수 차감
        redisUtil.decrement("coupon:" + serviceRequest.couponId());

        // 0개 이하라면, 취소
        String amount = redisUtil.get("coupon:" + serviceRequest.couponId());
        if (Integer.parseInt(amount) < 0) {
            redisUtil.increment("coupon:" + serviceRequest.couponId());
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_POSSIBLE_ISSUE_COUPON_BY_INSUFFICIENT_NUMBER);
        }

        CouponHistory couponHistory = couponHistoryRepository.register(CouponHistory.create(coupon.getId(), member.getId()));

        return IssueCouponResponse.from(couponHistory);
    }


}
