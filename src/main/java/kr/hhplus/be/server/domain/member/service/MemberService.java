package kr.hhplus.be.server.domain.member.service;

import kr.hhplus.be.server.domain.member.entity.Member;
import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.member.service.request.RegisterMemberServiceRequest;
import kr.hhplus.be.server.domain.member.service.response.MemberResponse;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Transactional
    public MemberResponse register(RegisterMemberServiceRequest request) {
        // 멤버 생성
        Member member = Member.register(request);
        memberRepository.save(member);

        // 멤버의 포인트 생성
        Point point = Point.register(member.getId());
        pointRepository.save(point);

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse retrieve(Long memberId) {
        // 회원 찾기
        Member member = memberRepository.findMemberByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));

        return MemberResponse.from(member);
    }


    @Transactional
    public void remove(Long id) {
        // 회원 찾기
        Member member = memberRepository.findMemberByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));
        // 회원 삭제
        member.delete();

        // 회원 Id로 포인트 찾기
        Point point = pointRepository.findPointByMemberIdAndDeletedFalse(member.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));
        // 포인트 삭제
        point.delete();
    }



}