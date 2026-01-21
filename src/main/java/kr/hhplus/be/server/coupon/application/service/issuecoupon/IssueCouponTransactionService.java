package kr.hhplus.be.server.coupon.application.service.issuecoupon;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IssueCouponTransactionService {

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    @Transactional
    public IssueCouponResponse issueCouponLockInternal(Long memberId, Long couponId) {

        // 쿠폰 찾기
        Coupon coupon = couponRepository.retrieveForUpdate(couponId);

        // 멤버한테 해당 쿠폰이 이미 있는지 확인
        Optional<CouponHistory> couponHistory = couponHistoryRepository.retrieveCouponHistory(memberId, coupon.getId());

        // 있으면 예외
        if (couponHistory.isPresent()) {
            throw new BusinessLogicRuntimeException(BusinessLogicMessage.ALREADY_HAVE_THIS_COUPON);
        }

        // 쿠폰 발행(개수 검증 도메인에서 됨)
        coupon.issue();
        couponRepository.modify(coupon);

        // 쿠폰 발행 내역 생성
        CouponHistory savedCouponHistory = couponHistoryRepository.register(CouponHistory.create(coupon.getId(), memberId));

        return IssueCouponResponse.from(savedCouponHistory);
    }
}
