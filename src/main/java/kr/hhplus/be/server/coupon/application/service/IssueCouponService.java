package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.request.IssueCouponServiceRequest;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.presentation.dto.response.IssueCouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueCouponService implements IssueCouponUseCase {

    private final CouponRepository couponRepository;
    // 쿠폰 히스토리

    @Override
    @Transactional
    public IssueCouponResponse issue(IssueCouponServiceRequest serviceRequest) {
        // 멤버 찾기

        // 쿠폰 찾기

        // 멤버한테 해당 쿠폰이 이미 있는지 확인


        // 쿠폰 발행(개수 검증 두메인에서 됨)

        // 쿠폰 발행 -> 테스트 코드 작성 : 선착순발행
        return null;
    }
}
