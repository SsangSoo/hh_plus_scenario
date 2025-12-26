package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.member.application.usecase.RemoveMemberUseCase;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class RetrieveMemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointRepository pointRepository;

    RemoveMemberUseCase removeMemberService;


    @BeforeEach
    void setUp() {
        removeMemberService = new RemoveMemberService(memberRepository, pointRepository);
    }

    @Test
    @DisplayName("삭제된 회원은 조회되지 않는다.")
    void cannotFoundDeletedMemberTest() {
        // given
        Long memberId = 1L;
        willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER))
                .given(memberRepository).remove(anyLong());

        // when // then  회원 조회시 예외 발생: 회원 정보 확인
        assertThatThrownBy(() -> removeMemberService.remove(memberId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER.getMessage());
    }
}
