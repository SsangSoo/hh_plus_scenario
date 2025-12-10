package kr.hhplus.be.server.domain.orderproduct.service.request;

public record OrderProductServiceRequest(
        Long productId,
        Long quantity
) {
}
