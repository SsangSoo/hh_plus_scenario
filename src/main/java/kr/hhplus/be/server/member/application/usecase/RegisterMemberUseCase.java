package kr.hhplus.be.server.member.application.usecase;

import kr.hhplus.be.server.member.application.dto.MemberResult;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;

public interface RegisterMemberUseCase {

    MemberResult register(RegisterMemberCommand request);
}
