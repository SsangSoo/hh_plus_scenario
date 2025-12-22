package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievePointService implements RetrievePointUseCase {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Override
    @Transactional
    public PointResponse retrieve(Long memberId) {
        // 회원 찾기
        memberRepository.retrieve(memberId);

        // 회원 Id로 포인트 찾기
        Point point = pointRepository.findByMemberId(memberId);
        return PointResponse.from(point);
    }
}
