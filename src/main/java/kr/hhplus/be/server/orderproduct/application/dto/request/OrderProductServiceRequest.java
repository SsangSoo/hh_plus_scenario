package kr.hhplus.be.server.orderproduct.application.dto.request;

public record OrderProductServiceRequest(
        Long productId,
        Long quantity
) {
}
