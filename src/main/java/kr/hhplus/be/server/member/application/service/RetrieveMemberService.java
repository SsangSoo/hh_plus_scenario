package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.member.application.dto.MemberResult;
import kr.hhplus.be.server.member.application.usecase.RetrieveMemberUseCase;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveMemberService implements RetrieveMemberUseCase {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberResult retrieve(Long memberId) {
        // 회원 찾기
        Member member = memberRepository.findById(memberId);

        return MemberResult.from(member);
    }
}
