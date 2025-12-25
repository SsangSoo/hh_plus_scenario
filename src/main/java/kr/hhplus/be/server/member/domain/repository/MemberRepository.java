package kr.hhplus.be.server.member.domain.repository;

import kr.hhplus.be.server.member.domain.model.Member;

public interface MemberRepository {
    Member save(Member member);

    Member retrieve(Long id);

    void remove(Long id);
}
