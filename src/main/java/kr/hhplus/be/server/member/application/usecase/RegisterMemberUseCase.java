package kr.hhplus.be.server.member.application.usecase;

import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;

public interface RegisterMemberUseCase {

    MemberResponse register(RegisterMemberCommand request);
}
