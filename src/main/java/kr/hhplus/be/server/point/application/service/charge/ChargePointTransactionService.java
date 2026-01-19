package kr.hhplus.be.server.point.application.service.charge;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 포인트 충전 트랜잭션 서비스
 *
 * 분산락 획득 후 트랜잭션 내에서 포인트 충전 로직을 수행합니다.
 * DB Pessimistic Lock과 병행하여 Defense in Depth 전략을 적용합니다.
 */
@Component
@RequiredArgsConstructor
public class ChargePointTransactionService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 포인트 충전 (트랜잭션 내부)
     *
     * @param chargePoint 충전 정보
     * @return 포인트 응답
     */
    @Transactional
    public PointResponse chargeInternal(ChargePoint chargePoint) {
        // 회원 찾기
        Member member = memberRepository.retrieve(chargePoint.memberId());

        // 포인트 충전 (DB Lock 획득 - Defense in Depth)
        Point point = pointRepository.findByMemberIdForUpdate(chargePoint.memberId());
        point.charge(chargePoint);
        LocalDateTime modifiedTime = pointRepository.modify(point.getId(), point.getPoint());

        // 포인트 충전 내역 저장
        PointHistory pointHistory = PointHistory.create(
                member.getId(),
                point.getId(),
                chargePoint.point(),
                modifiedTime,
                point.getPoint(),
                State.CHARGE
        );
        pointHistoryRepository.save(pointHistory);

        return PointResponse.from(point);
    }
}
