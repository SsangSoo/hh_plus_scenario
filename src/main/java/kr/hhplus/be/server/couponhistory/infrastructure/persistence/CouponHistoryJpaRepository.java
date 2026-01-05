package kr.hhplus.be.server.couponhistory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CouponHistoryJpaRepository extends JpaRepository<CouponHistoryJpaEntity, Long> {

    Optional<CouponHistoryJpaEntity> findByMemberIdAndCouponId(Long memberId, Long couponId);
}
