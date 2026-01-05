package kr.hhplus.be.server.coupon.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CouponJpaEntity c where c.id = :id")
    Optional<CouponJpaEntity> findByIdForUpdate(Long id);

}
