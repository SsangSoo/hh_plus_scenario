package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
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
    public MemberResponse register(RegisterMemberCommand request) {
        // 멤버 생성
        Member member = memberRepository.save(Member.create(request));

        // 멤버의 포인트 생성
        pointRepository.save(Point.create(member.getId()));

        return MemberResponse.from(member);
    }

}