package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueCouponService implements IssueCouponUseCase {

    private final CouponRepository couponRepository;

    private final MemberRepository memberRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    @Override
    @Transactional
    public IssueCouponResponse issue(IssueCouponServiceRequest serviceRequest) {
        // 멤버 찾기
        Member member = memberRepository.retrieve(serviceRequest.memberId());

        // 쿠폰 찾기
        Coupon coupon = couponRepository.retrieveForUpdate(serviceRequest.couponId());

        // 멤버한테 해당 쿠폰이 이미 있는지 확인
        Optional<CouponHistory> couponHistory = couponHistoryRepository.retrieveCouponHistory(member.getId(), coupon.getId());
        // 있으면 예외
        if(couponHistory.isPresent()) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.ALREADY_HAVE_THIS_COUPON);
        }

        // 쿠폰 발행(개수 검증 도메인에서 됨)
        coupon.issue();
        couponRepository.modify(coupon);

        // 쿠폰 발행 선착순발행
        CouponHistory savedCouponHistory = couponHistoryRepository.register(CouponHistory.create(coupon.getId(), member.getId()));

        return IssueCouponResponse.from(savedCouponHistory);
    }
}
