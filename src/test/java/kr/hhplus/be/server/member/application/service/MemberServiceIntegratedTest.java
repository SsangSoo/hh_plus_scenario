package kr.hhplus.be.server.member.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MemberServiceIntegratedTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원을 생성하고, 포인트를 충전하고, 충전한 포인트가 잘 조회되는지 테스트")
    void pointChargeAndRetrieveTest() {
        // given : 회원 생성 및 포인트 충전
        Long chargePoint = 1000L;
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberCommand("내이름", LocalDate.of(1990, 1, 1).toString(), "주소"));

        PointResponse pointResponse = retrievePointUseCase.retrieve(memberResponse.getId());
        assertThat(pointResponse.getPoint()).isZero();

        chargePointUseCase.charge(new ChargePoint(memberResponse.getId(), chargePoint));

        // when  : 충전된 포인트 조회
        pointResponse = retrievePointUseCase.retrieve(memberResponse.getId());

        // then  : 포인트가 잘 충전되었는지 확인
        assertThat(pointResponse.getPoint()).isEqualTo(chargePoint);
    }

    @Test
    @DisplayName("회원 정보를 조회한다.")
    void retrieveTest() {
        // given
        RegisterMemberCommand request = new RegisterMemberCommand("내이름", LocalDate.of(1990, 1, 1).toString(), "주소");
        Member member = Member.create(request);
        Member savedMember = memberRepository.save(member);

        // when : 회원 조회
        MemberResponse response = retrieveMemberUseCase.retrieve(savedMember.getId());

        // then : 회원 정보 확인
        assertThat(response.getName()).isEqualTo(request.name());
        assertThat(response.getBirthDate()).isEqualTo(request.birthDate());
        assertThat(response.getAddress()).isEqualTo(request.address());
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void removeMember() {
        // given
        MemberResponse registeredMember = registerMemberUseCase.register(new RegisterMemberCommand("이름", "19900202", "주소"));

        // when
        removeMemberUseCase.remove(registeredMember.getId());

        // then
        assertThatThrownBy(() -> retrieveMemberUseCase.retrieve(registeredMember.getId()))
                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER.getMessage())
                .isInstanceOf(BusinessLogicRuntimeException.class);
    }




}