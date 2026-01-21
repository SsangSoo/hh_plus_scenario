package kr.hhplus.be.server.couponhistory.application.service;

import kr.hhplus.be.server.couponhistory.application.usecase.UseCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UseCouponHistoryService implements UseCouponHistoryUseCase {

    private final CouponHistoryRepository couponHistoryRepository;

    @Override
    @Transactional
    public void couponUse(CouponHistory couponHistory) {
        couponHistoryRepository.modifyByUsing(couponHistory);
    }

    @Override
    public void couponUseRollback(CouponHistory couponHistory) {
        couponHistoryRepository.modifyByUsing(couponHistory);

    }
}
