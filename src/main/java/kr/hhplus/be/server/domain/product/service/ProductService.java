package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import kr.hhplus.be.server.domain.stock.entity.Stock;
import kr.hhplus.be.server.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional
    public ProductResponse registerProduct(RegisterProductServiceRequest request) {
        // 1. 상품 생성
        log.info("상품 생성");
        Product product = Product.register(request);
        productRepository.save(product);

        // 2. 상품 재고 생성
        log.info("상품 재고 생성");
        Stock stock = Stock.register(product.getId());
        stockRepository.save(stock);

        // 반환
        return ProductResponse.from(product, stock.getQuantity());
    }


    @Transactional
    public void removeProduct(Long productId) {
        // 상품 확인
        Product product = productRepository.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));
        product.delete();

        // 재고 확인
        Stock stock = stockRepository.findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));
        stock.delete();

    }

    @Transactional(readOnly = true)
    public ProductResponse retrieveProduct(Long productId) {
        // 1. 상품 조회
        Product product = productRepository.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // 2. 재고 조회
        Long quantity = productRepository.retrieveStockByProductId(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));

        // 3. 반환
        return ProductResponse.from(product, quantity);
    }
}
