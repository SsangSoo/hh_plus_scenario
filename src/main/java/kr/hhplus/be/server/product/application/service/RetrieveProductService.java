package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.RetrieveProductUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveProductService implements RetrieveProductUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public ProductResponse retrieveProduct(Long productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId);

        // 2. 재고 조회
        Long quantity = stockRepository.retrieveStockByProductId(productId);

        // 3. 반환
        return ProductResponse.from(product, quantity);
    }
}
