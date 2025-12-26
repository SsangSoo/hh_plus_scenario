package kr.hhplus.be.server.product.application.usecase;

import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;

public interface RetrieveProductUseCase {

    ProductResponse retrieveProduct(Long productId);
}
