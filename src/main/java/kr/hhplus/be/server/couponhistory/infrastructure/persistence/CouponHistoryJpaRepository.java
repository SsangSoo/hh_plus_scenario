package kr.hhplus.be.server.couponhistory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface CouponHistoryJpaRepository extends JpaRepository<CouponHistoryJpaEntity, Long> {

    Optional<CouponHistoryJpaEntity> findByMemberIdAndCouponId(Long memberId, Long couponId);

    Optional<CouponHistoryJpaEntity> findByMemberIdAndCouponIdAndCouponUsedIsFalse(Long memberId, Long couponId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE " +
            "   CouponHistoryJpaEntity che " +
            "SET che.couponUsed = " +
            "   CASE " +
            "       WHEN che.couponUsed = true THEN false " +
            "       ELSE true" +
            "   END " +
            "   WHERE " +
            "       che.couponId = :couponId AND " +
            "       che.memberId = :memberId")
    void updateCouponUsed(Long memberId, Long couponId);
}
