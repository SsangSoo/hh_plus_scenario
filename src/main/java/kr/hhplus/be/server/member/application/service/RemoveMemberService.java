package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.member.application.usecase.RemoveMemberUseCase;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.repository.PointRepository;
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
        // 회원 찾기
        Member member = memberRepository.findById(id);

        // 회원 삭제
        member.delete();
        memberRepository.delete(member);

        // 회원 Id로 포인트 찾기
        Point point = pointRepository.findPointByMemberIdAndDeletedFalse(member.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        // 포인트 삭제
        point.delete();
    }

}
