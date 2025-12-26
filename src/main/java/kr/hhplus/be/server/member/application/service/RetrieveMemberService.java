package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.member.application.usecase.RetrieveMemberUseCase;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveMemberService implements RetrieveMemberUseCase {

    private final MemberRepository memberRepository;

    @Override
    public MemberResponse retrieve(Long memberId) {
        // 회원 찾기
        Member member = memberRepository.retrieve(memberId);

        return MemberResponse.from(member);
    }
}
