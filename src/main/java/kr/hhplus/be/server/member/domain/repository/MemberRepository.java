package kr.hhplus.be.server.member.domain.repository;

import kr.hhplus.be.server.member.domain.Member;

public interface MemberRepository {
    Member save(Member member);

    Member findById(Long id);

    void delete(Member member);
}
