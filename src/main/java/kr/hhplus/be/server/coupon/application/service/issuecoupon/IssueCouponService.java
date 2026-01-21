package kr.hhplus.be.server.coupon.application.service.issuecoupon;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCouponService implements IssueCouponUseCase {

    private final RedissonClient redissonClient;
    private final MemberRepository memberRepository;
    private final IssueCouponTransactionService  issueCouponTransactionService;

    public IssueCouponResponse issue(IssueCouponServiceRequest serviceRequest) {

        // 멤버 확인
        Member member = memberRepository.retrieve(serviceRequest.memberId());

        // 분산락 생성 (쿠폰ID 기반)
        RLock lock = redissonClient.getLock("couponIssue:" + serviceRequest.couponId());

        try {
            // 분산락 획득: 대기시간 300초 (선착순 시나리오 대응), watchdog 자동 연장 (-1)
            boolean available = lock.tryLock(300, -1, TimeUnit.SECONDS);

            if(!available) {
                log.warn("쿠폰 발급 Lock 획득 실패 - couponId: {}, memberId: {}",
                         serviceRequest.couponId(), member.getId());
                throw new IllegalStateException("쿠폰 발급 Lock 획득 실패, couponId = " + serviceRequest.couponId() + ", memberId = " + member.getId());
            }

            return issueCouponTransactionService.issueCouponLockInternal(member.getId(), serviceRequest.couponId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("쿠폰 발급 중 인터럽트 발생", e);
        } catch (BusinessLogicRuntimeException e) {
            throw e;
        } finally {
            // Lock 해제 (안전하게 처리)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
