package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterProductService implements RegisterProductUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public ProductResponse register(RegisterProductServiceRequest request) {
        // 1. 상품 생성
        Product product = productRepository.save(Product.create(request));

        // 2. 상품 재고 생성
        Stock stock = stockRepository.save(Stock.create(product.getId()));

        // 반환
        return ProductResponse.from(product, stock.getQuantity());
    }

}
