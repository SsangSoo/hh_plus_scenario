package kr.hhplus.be.server.outbox.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity,Long> {


    Optional<OutboxJpaEntity> findByOrderId(Long orderId);

}
