package kr.hhplus.be.server.member.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaImpl implements MemberRepository {

    private final MemberJpaRepository jpa;

    @Override
    public Member save(Member member) {
        MemberJpaEntity memberJpaEntity = MemberJpaEntity.from(member);
        MemberJpaEntity saved = jpa.save(memberJpaEntity);
        return saved.toDomain();
    }

    @Override
    public Member retrieve(Long id) {
        MemberJpaEntity memberJpaEntity = jpa.findByIdAndRemovedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));
        return memberJpaEntity.toDomain();
    }

    @Override
    public void remove(Long memberId) {
        MemberJpaEntity memberJpaEntity = jpa.findByIdAndRemovedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));
        memberJpaEntity.remove();
    }
}
