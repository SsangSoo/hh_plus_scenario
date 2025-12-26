package kr.hhplus.be.server.product.application.dto.request;

public record RegisterProductServiceRequest(
        String productName,
        Long price
) {
}
