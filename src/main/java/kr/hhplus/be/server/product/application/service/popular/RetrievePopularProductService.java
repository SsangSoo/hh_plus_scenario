package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.product.application.usecase.popular.RetrievePopularProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrievePopularProductService implements RetrievePopularProductUseCase {

    private final RedisUtil redisUtil;
    private final ProductQueryRepository productQueryRepository;

    private static final String POPULAR_PRODUCT_KEY = "SELL:PRODUCT:RANKING";

    @Override
    public List<ProductResponse> retrievePopularProducts() {

        // Redis에 등록된 순서대로 상품 가져오기
        List<Long> idList = redisUtil.getZSetReverseRange(POPULAR_PRODUCT_KEY, 0, 9)
                .stream()
                .map(Long::valueOf)
                .toList();

        // 상품 Id로 상품 조회
        List<ProductProjection> products = productQueryRepository.retrieveProductsInIds(idList);

        // Map을 통해서 상품Id:상품으로 정렬
        Map<Long, ProductProjection> productMap = products.stream()
                .collect(Collectors.toMap(ProductProjection::getId, Function.identity()));

        // Response로 리턴
        return idList.stream()
                .map(productMap::get)
                .map(ProductResponse::from)
                .toList();
    }
}
