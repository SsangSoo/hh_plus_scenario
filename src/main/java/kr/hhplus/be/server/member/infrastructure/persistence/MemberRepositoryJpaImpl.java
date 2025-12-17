package kr.hhplus.be.server.member.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaImpl implements MemberRepository {

    private final MemberJpaRepository jpa;

    @Override
    public Member save(Member member) {
        MemberJpaEntity memberJpaEntity = MemberJpaEntity.from(member);
        MemberJpaEntity saved = jpa.save(memberJpaEntity);
        member.assignId(saved.getId());
        return member;
    }

    @Override
    public Member findById(Long id) {
        MemberJpaEntity memberJpaEntity = jpa.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));
        return memberJpaEntity.toDomain();
    }

    @Override
    public void delete(Member member) {
        MemberJpaEntity memberJpaEntity = MemberJpaEntity.from(member);
        jpa.save(memberJpaEntity)
    }
}
