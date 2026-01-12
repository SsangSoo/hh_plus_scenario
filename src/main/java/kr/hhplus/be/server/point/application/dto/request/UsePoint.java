package kr.hhplus.be.server.point.application.dto.request;

public record UsePoint(
        Long memberId,
        Long point
) {

    public UsePoint(Long memberId, Long point) {
        if(point == null || point <= 0) {
            throw new IllegalStateException("사용하려는 포인트는 0보다 커야 합니다.");
        }
        if(memberId == null || memberId <= 0) {
            throw new IllegalStateException("회원 Id는 유효해야 합니다.");
        }
        this.memberId = memberId;
        this.point = point;
    }

}
