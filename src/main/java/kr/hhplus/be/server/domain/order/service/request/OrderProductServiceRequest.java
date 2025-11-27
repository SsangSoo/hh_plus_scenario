package kr.hhplus.be.server.domain.order.service.request;

public record OrderProductServiceRequest(
        Long productId,
        Long quantity
) {
}
