package kr.hhplus.be.server.point.application.dto.request;

public record ChargePoint(
        Long memberId,
        Long point
) {

    public ChargePoint(Long memberId, Long point) {
        if(point <= 0) {
            throw new IllegalStateException("충전하려는 포인트는 0 이상이어야 합니다.");
        }
        this.memberId = memberId;
        this.point = point;
    }

}