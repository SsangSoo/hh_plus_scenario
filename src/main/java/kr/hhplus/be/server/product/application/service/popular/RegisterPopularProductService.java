package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.product.application.usecase.popular.RegisterPopularProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterPopularProductService implements RegisterPopularProductUseCase {

    private final RedisUtil redisUtil;
    private final ProductQueryRepository productQueryRepository;

    private static final String POPULAR_PRODUCT_KEY = "SELL:PRODUCT:RANKING";

    @Override
    @Async
    @Transactional(readOnly = true)
    public void registerPopularProducts(Long orderId, Long memberId) {

        // orderId로 상품 Id들 알아냄.
        List<Long> productIdList = productQueryRepository.retrieveProductIdWithOrderId(orderId);

        // Redis로 상품 Id로 등록
        // 일단 지금은 판매 건수로 인기 상품 API 구현
            // 추후 재구매율로도 인기 상품 API 구현하도록 하기
        for (Long productId : productIdList) {
            redisUtil.incrementZSetScore(POPULAR_PRODUCT_KEY, String.valueOf(productId), 1);
        }

        // Redis로 재구매율을 위해 등록
//        for (Long productId : productIdList) {
//            redisUtil.increment("SELL:PRODUCT:" + productId + ":MEMBER:" + memberId);
//        }
    }

}
