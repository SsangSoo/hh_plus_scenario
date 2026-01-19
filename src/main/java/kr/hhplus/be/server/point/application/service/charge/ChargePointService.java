package kr.hhplus.be.server.point.application.service.charge;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 포인트 충전 서비스
 *
 * 분산락을 통해 멀티 인스턴스 환경에서 포인트 동시성을 제어합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointService implements ChargePointUseCase {

    private final RedissonClient redissonClient;
    private final MemberRepository memberRepository;
    private final ChargePointTransactionService transactionService;

    @Override
    public PointResponse charge(ChargePoint chargePoint) {
        // 회원 확인
        Member member = memberRepository.retrieve(chargePoint.memberId());

        // 분산락 생성 (회원ID 기반)
        RLock lock = redissonClient.getLock("point:lock:" + member.getId());

        try {
            // 분산락 획득: 대기시간 2초, 자동 해제 3초
            boolean available = lock.tryLock(2, 3, TimeUnit.SECONDS);

            if (!available) {
                log.warn("포인트 충전 Lock 획득 실패 - memberId: {}", member.getId());
                throw new IllegalStateException("포인트 충전 Lock 획득 실패: memberId=" + member.getId());
            }

            return transactionService.chargeInternal(chargePoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("포인트 충전 중 인터럽트 발생", e);
        } finally {
            // Lock 해제 (안전하게 처리)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}