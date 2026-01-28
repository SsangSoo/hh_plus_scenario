package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.RetrievePopularProductUseCase;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievePopularProductService implements RetrievePopularProductUseCase {

    @Override
    public List<ProductResponse> retrievePopularProducts() {
        return List.of();
    }
}
