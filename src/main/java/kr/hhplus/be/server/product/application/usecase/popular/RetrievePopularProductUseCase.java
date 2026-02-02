package kr.hhplus.be.server.product.application.usecase.popular;

import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;

import java.util.List;

public interface RetrievePopularProductUseCase {

    List<ProductResponse> retrievePopularProducts();

}
