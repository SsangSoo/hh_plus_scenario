package kr.hhplus.be.server.point.repository;

import kr.hhplus.be.server.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point,Long> {

    Optional<Point> findPointByMemberIdAndDeletedFalse(Long memberId);

    @Query("select m.id from MemberJpaEntity m where m.id = :id and m.deleted = false")
    Optional<Long> findMemberIdByMemberId(Long id);

}
