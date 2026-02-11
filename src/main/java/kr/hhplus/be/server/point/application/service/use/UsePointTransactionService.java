package kr.hhplus.be.server.point.application.service.use;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.dto.request.UsePoint;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 포인트 사용 트랜잭션 서비스
 *
 * 분산락 획득 후 트랜잭션 내에서 포인트 사용 로직을 수행합니다.
 * DB Pessimistic Lock과 병행하여 Defense in Depth 전략을 적용합니다.
 */
@Component
@RequiredArgsConstructor
public class UsePointTransactionService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 포인트 사용 (트랜잭션 내부)
     *
     * @param usePoint 사용 정보
     * @return 포인트 응답
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PointResponse useInternal(UsePoint usePoint) {
        Point point = pointRepository.findByMemberId(usePoint.memberId());

        point.use(usePoint.point());

        LocalDateTime modifiedTime = pointRepository.modify(point.getId(), point.getPoint());

        // 포인트 사용 내역 저장
        PointHistory pointHistory = PointHistory.create(
                usePoint.memberId(),
                point.getId(),
                usePoint.point(),
                modifiedTime,
                point.getPoint(),
                State.USE
        );
        pointHistoryRepository.save(pointHistory);

        return PointResponse.from(point);
    }

}