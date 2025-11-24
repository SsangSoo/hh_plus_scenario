package kr.hhplus.be.server.domain.product.service.request;

public record RegisterProductServiceRequest(
        String productName,
        Long quantity
) {
}
