package kr.hhplus.be.server.coupon.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {

    // 분산락으로 동시성 제어 (X-Lock 비활성화)
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CouponJpaEntity c where c.id = :id")
    Optional<CouponJpaEntity> findByIdForUpdate(Long id);

}
