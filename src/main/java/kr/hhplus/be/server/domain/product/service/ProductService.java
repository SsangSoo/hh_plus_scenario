package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.global.exception.BusinessLogicRuntimeException;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.hhplus.be.server.domain.global.exception.BusinessLogicMessage.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public ProductResponse retrieve(Long productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(NOT_FOUND_PRODUCT));

        // 2. 재고 조회
        Optional<Long> quantity = productRepository.retrieveStockByProductId(productId);

        if(quantity.isEmpty()) {

        }


        // 3. 반환
        return ProductResponse.from(product, quantity.orElse(null));
    }
}
