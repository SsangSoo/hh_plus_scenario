package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.dto.request.UsePoint;
import kr.hhplus.be.server.point.application.usecase.UsePointUseCase;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsePointService implements UsePointUseCase {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public PointResponse use(UsePoint usePoint) {
        // 회원 찾기
        Member member = memberRepository.retrieve(usePoint.memberId());

        // 포인트 사용 (Pessimistic Lock 적용)
        Point point = pointRepository.findByMemberIdForUpdate(usePoint.memberId());
        point.use(usePoint.point());
        LocalDateTime modifiedTime = pointRepository.update(point.getId(), point.getPoint());

        // 포인트 사용 내역 저장
        PointHistory pointHistory = PointHistory.create(member.getId(), point.getId(), usePoint.point(), modifiedTime, point.getPoint(), State.USE);
        pointHistoryRepository.save(pointHistory);

        return PointResponse.from(point);
    }
}
