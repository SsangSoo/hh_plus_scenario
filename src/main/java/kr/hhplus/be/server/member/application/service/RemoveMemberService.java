package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.member.application.usecase.RemoveMemberUseCase;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveMemberService implements RemoveMemberUseCase {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Override
    @Transactional
    public void remove(Long id) {
        // 회원 삭제
        memberRepository.remove(id);

        // 포인트 삭제
        Point point = pointRepository.findByMemberId(id);
        pointRepository.remove(point.getId());
    }

}
