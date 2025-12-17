package kr.hhplus.be.server.product.service.request;

public record RegisterProductServiceRequest(
        String productName,
        Long price
) {
}
