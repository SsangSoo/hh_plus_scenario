package kr.hhplus.be.server.domain.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringOrderJpa extends JpaRepository<OrderEntity, Long> {
}
