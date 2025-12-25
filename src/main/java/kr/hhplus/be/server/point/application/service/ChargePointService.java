package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.pointhistory.domain.model.PointHistory;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import kr.hhplus.be.server.pointhistory.domain.model.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargePointService implements ChargePointUseCase {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public PointResponse charge(ChargePoint chargePoint) {
        // 회원 찾기
        Member member = memberRepository.retrieve(chargePoint.memberId());

        // 포인트 충전
        Point point = pointRepository.findByMemberId(chargePoint.memberId());
        point.charge(chargePoint);
        LocalDateTime modifiedTime = pointRepository.update(point.getId(), point.getPoint());

        // 포인트 충전 내역 저장
        PointHistory pointHistory = PointHistory.create(member.getId(), point.getId(), chargePoint.point(), modifiedTime, point.getPoint(), State.CHARGE);
        pointHistoryRepository.save(pointHistory);

        return PointResponse.from(point);
    }
 }