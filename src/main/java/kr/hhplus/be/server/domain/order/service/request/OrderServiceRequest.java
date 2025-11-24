package kr.hhplus.be.server.domain.order.service.request;

public record OrderServiceRequest (
        Long memberId,
        OrderProductServiceRequest orderProductRequest
) {

}
