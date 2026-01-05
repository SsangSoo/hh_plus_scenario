package kr.hhplus.be.server.couponhistory.infrastructure.persistence;

import kr.hhplus.be.server.couponhistory.domain.model.CouponHistory;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponHistoryRepositoryImpl implements CouponHistoryRepository {

    private final CouponHistoryJpaRepository jpa;

    @Override
    public CouponHistory register(CouponHistory couponHistory) {
        CouponHistoryJpaEntity saved = jpa.save(CouponHistoryJpaEntity.from(couponHistory));
        return saved.toDomain();
    }

    @Override
    public Optional<CouponHistory> retrieveCouponHistory(Long memberId, Long couponId) {
        Optional<CouponHistoryJpaEntity> findCouponHistory = jpa.findByMemberIdAndCouponId(memberId, couponId);
        return findCouponHistory.map(CouponHistoryJpaEntity::toDomain);
    }
}
