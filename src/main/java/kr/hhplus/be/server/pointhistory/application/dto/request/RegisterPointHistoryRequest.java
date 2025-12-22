package kr.hhplus.be.server.pointhistory.application.dto.request;

import java.time.LocalDateTime;

public record RegisterPointHistoryRequest(

    Long memberId,
    Long pointId,
    Long pointAmount,
    LocalDateTime createdDate,
    Long totalPoint
    ) {

}
