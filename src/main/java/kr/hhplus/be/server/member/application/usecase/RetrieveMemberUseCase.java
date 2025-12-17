package kr.hhplus.be.server.member.application.usecase;

import kr.hhplus.be.server.member.application.dto.MemberResult;

public interface RetrieveMemberUseCase {

    MemberResult retrieve(Long memberId);
}
