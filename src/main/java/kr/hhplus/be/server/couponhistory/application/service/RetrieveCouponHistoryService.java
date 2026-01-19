package kr.hhplus.be.server.couponhistory.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.couponhistory.application.usecase.RetrieveCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RetrieveCouponHistoryService implements RetrieveCouponHistoryUseCase {

    private final CouponHistoryRepository couponHistoryRepository;


    @Override
    @Transactional(readOnly = true)
    public CouponHistory retrieveCouponHistory(Long memberId, Long couponId) {
        return couponHistoryRepository.retrieveCouponHistory(memberId, couponId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
    }
}
