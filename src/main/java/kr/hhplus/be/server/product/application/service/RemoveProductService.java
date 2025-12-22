package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.RemoveProductUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveProductService implements RemoveProductUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void removeProduct(Long productId) {
        // 상품 확인
        Product product = productRepository.findById(productId);
        productRepository.remove(product.getId());

        // 재고 확인
        Stock stock = stockRepository.findByProductId(productId);
        stockRepository.remove(stock.getId());
    }
}
