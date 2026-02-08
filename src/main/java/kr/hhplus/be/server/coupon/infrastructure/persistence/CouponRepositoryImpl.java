package kr.hhplus.be.server.coupon.infrastructure.persistence;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository jpa;

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity saved = jpa.save(CouponJpaEntity.from(coupon));
        return saved.toDomain();
    }

    @Override
    public List<Coupon> saveList(List<Coupon> coupons) {
        List<CouponJpaEntity> mappedCouponJpaEntities = coupons.stream()
                .map(CouponJpaEntity::from)
                .toList();
        List<CouponJpaEntity> savedCouponJpaEntities = jpa.saveAll(mappedCouponJpaEntities);
        return savedCouponJpaEntities.stream()
                .map(CouponJpaEntity::toDomain)
                .toList();

    }

    @Override
    public Coupon retrieve(Long couponId) {
        CouponJpaEntity couponJpaEntity = jpa.findById(couponId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
        return couponJpaEntity.toDomain();
    }

    @Override
    public Coupon retrieveForUpdate(Long couponId) {
        CouponJpaEntity couponJpaEntity = jpa.findByIdForUpdate(couponId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
        return couponJpaEntity.toDomain();
    }

    @Override
    public Coupon modify(Coupon coupon) {
        CouponJpaEntity couponJpaEntity = jpa.findById(coupon.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
        couponJpaEntity.modify(coupon.getAmount());
        return couponJpaEntity.toDomain();
    }

    @Override
    public void decrease(Long couponId) {
        CouponJpaEntity couponJpaEntity = jpa.findByIdForUpdate(couponId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_COUPON));
        couponJpaEntity.decrease();
    }


}
