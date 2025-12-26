package kr.hhplus.be.server.member.application.usecase;

import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;

public interface RetrieveMemberUseCase {

    MemberResponse retrieve(Long memberId);
}
