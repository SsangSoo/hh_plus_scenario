package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.product.application.usecase.RetrieveProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveProductService implements RetrieveProductUseCase {

    private final ProductQueryRepository productQueryRepository;

    @Transactional(readOnly = true)
    public ProductResponse retrieveProduct(Long productId) {

        // queryDSL 사용
        ProductProjection productProjection = productQueryRepository.retrieveProductJoinStock(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // 3. 반환
        return ProductResponse.from(productProjection);
    }
}
