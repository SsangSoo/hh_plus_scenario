package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterMemberService implements RegisterMemberUseCase {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Override
    @Transactional
    public MemberResult register(RegisterMemberCommand request) {
        // 멤버 생성
        Member savedMember = memberRepository.save(Member.create(request));

        // 멤버의 포인트 생성
        Point point = Point.register(savedMember.getId());
        pointRepository.save(point);

        return MemberResult.from(savedMember);
    }


}