package kr.hhplus.be.server.point.service.response;

import kr.hhplus.be.server.point.entity.Point;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointResponse {

    private Long id;
    private Long memberId;
    private Long point;

    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .id(point.getId())
                .memberId(point.getMemberId())
                .point(point.getPoint())
                .build();
    }

    @Builder
    private PointResponse(Long id, Long memberId, Long point) {
        this.id = id;
        this.memberId = memberId;
        this.point = point;
    }
}
