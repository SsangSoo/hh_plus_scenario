package kr.hhplus.be.server.product.application.usecase;

import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;

public interface RegisterProductUseCase {

    ProductResponse register(RegisterProductServiceRequest request);
}
