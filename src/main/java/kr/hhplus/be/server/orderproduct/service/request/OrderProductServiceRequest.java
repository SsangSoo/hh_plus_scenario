package kr.hhplus.be.server.orderproduct.service.request;

public record OrderProductServiceRequest(
        Long productId,
        Long quantity
) {
}
